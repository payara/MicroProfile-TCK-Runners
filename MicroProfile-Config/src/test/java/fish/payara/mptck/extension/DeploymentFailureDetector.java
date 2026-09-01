/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2026 Payara Foundation and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://github.com/payara/Payara/blob/master/LICENSE.txt
 * See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at glassfish/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * The Payara Foundation designates this particular file as subject to the "Classpath"
 * exception as provided by the Payara Foundation in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
package fish.payara.mptck.extension;

import org.jboss.arquillian.container.spi.client.container.DeploymentException;
import org.jboss.arquillian.container.spi.client.deployment.DeploymentDescription;
import org.jboss.arquillian.container.spi.event.container.AfterDeploy;
import org.jboss.arquillian.container.spi.event.container.BeforeDeploy;
import org.jboss.arquillian.core.api.annotation.Observes;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Works around a limitation of the managed Payara Micro Arquillian connector
 * ({@code PayaraMicroDeployableContainer}). That connector decides deployment
 * success purely from Payara Micro's startup banner, so when a submitted
 * archive fails to deploy (for example when a broken {@code @ConfigProperty}
 * injection point makes the MicroProfile Config CDI extension throw a
 * {@link jakarta.enterprise.inject.spi.DeploymentException}), Micro still boots,
 * reports an empty {@code "Deployed": []} list and stays running. The connector
 * therefore returns success and never propagates the failure, which makes tests
 * annotated with {@code @ShouldThrowException(DeploymentException.class)} fail
 * with "no exception was thrown".
 *
 * <p>Payara Micro logs the failure to {@code System.out} (the connector pipes
 * the child process output there). This observer captures that output around
 * each deployment and, if Micro logged a deployment failure, rethrows it as a
 * {@link DeploymentException}. That exception is raised while
 * {@code AfterDeploy} is fired inside {@code ContainerDeployController.deploy},
 * so Arquillian's {@code DeploymentExceptionHandler} catches it and matches it
 * against the deployment's expected exception type.</p>
 */
public class DeploymentFailureDetector {

    private static final Logger LOG = Logger.getLogger(DeploymentFailureDetector.class.getName());

    /**
     * Marker Payara Micro prints when an application fails to load during
     * deployment (see {@code javax.enterprise.system.core} logging).
     */
    private static final String FAILURE_MARKER = "Exception while loading the app";

    private PrintStream originalOut;
    private CapturingPrintStream capturingOut;

    /**
     * Starts capturing {@code System.out} (while still passing everything
     * through to the real stream) just before the container is asked to deploy.
     */
    public void beforeDeploy(@Observes BeforeDeploy event) {
        PrintStream current = System.out;
        // Unwrap in case a previous deployment leaked its capturing stream.
        originalOut = current instanceof CapturingPrintStream
                ? ((CapturingPrintStream) current).getDelegate()
                : current;
        capturingOut = CapturingPrintStream.around(originalOut);
        System.setOut(capturingOut);
    }

    /**
     * Restores {@code System.out} and, if Payara Micro logged a deployment
     * failure for this archive, rethrows it so Arquillian can observe it.
     */
    public void afterDeploy(@Observes AfterDeploy event) throws DeploymentException {
        String output = "";
        if (capturingOut != null) {
            System.setOut(originalOut);
            output = capturingOut.getCaptured();
            capturingOut = null;
            originalOut = null;
        }

        if (!output.contains(FAILURE_MARKER)) {
            return;
        }

        DeploymentDescription deployment = event.getDeployment();
        String name = deployment.getName();
        LOG.log(Level.WARNING,
                "Payara Micro reported a deployment failure for ''{0}'' that the container "
                        + "connector did not propagate; surfacing it as a DeploymentException.", name);
        throw new DeploymentException(
                "Deployment of " + name + " failed in Payara Micro ('" + FAILURE_MARKER + "').",
                buildCause(deployment, output));
    }

    /**
     * Builds a cause whose type matches the deployment's expected exception when
     * one is declared (via {@code @ShouldThrowException}), so the expected-type
     * check in Arquillian's {@code DeploymentExceptionHandler} succeeds.
     */
    private Throwable buildCause(DeploymentDescription deployment, String output) {
        String message = extractFailureMessage(output);
        Class<? extends Exception> expected = deployment.getExpectedException();
        if (expected != null) {
            try {
                return expected.getConstructor(String.class).newInstance(message);
            } catch (ReflectiveOperationException | RuntimeException e) {
                LOG.log(Level.FINE, "Could not instantiate expected exception " + expected, e);
            }
        }
        return new IllegalStateException(message);
    }

    /**
     * Extracts the first line following the failure marker to use as a concise
     * cause message, falling back to the marker itself.
     */
    private String extractFailureMessage(String output) {
        int markerIndex = output.indexOf(FAILURE_MARKER);
        if (markerIndex < 0) {
            return FAILURE_MARKER;
        }
        String fromMarker = output.substring(markerIndex);
        int newline = fromMarker.indexOf('\n');
        String firstLine = newline < 0 ? fromMarker : fromMarker.substring(0, newline);
        return firstLine.trim();
    }

    /**
     * A {@link PrintStream} that forwards everything to a delegate stream while
     * also accumulating the bytes so they can be inspected afterwards.
     */
    static final class CapturingPrintStream extends PrintStream {

        private final PrintStream delegate;
        private final ByteArrayOutputStream buffer;

        private CapturingPrintStream(PrintStream delegate, ByteArrayOutputStream buffer) {
            super(new TeeOutputStream(delegate, buffer), true, StandardCharsets.UTF_8);
            this.delegate = delegate;
            this.buffer = buffer;
        }

        static CapturingPrintStream around(PrintStream delegate) {
            return new CapturingPrintStream(delegate, new ByteArrayOutputStream());
        }

        PrintStream getDelegate() {
            return delegate;
        }

        String getCaptured() {
            return buffer.toString(StandardCharsets.UTF_8);
        }
    }

    /**
     * Writes every byte to two underlying streams.
     */
    private static final class TeeOutputStream extends OutputStream {

        private final OutputStream first;
        private final OutputStream second;

        TeeOutputStream(OutputStream first, OutputStream second) {
            this.first = first;
            this.second = second;
        }

        @Override
        public void write(int b) throws IOException {
            first.write(b);
            second.write(b);
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            first.write(b, off, len);
            second.write(b, off, len);
            first.flush();
        }

        @Override
        public void flush() throws IOException {
            first.flush();
        }
    }
}
