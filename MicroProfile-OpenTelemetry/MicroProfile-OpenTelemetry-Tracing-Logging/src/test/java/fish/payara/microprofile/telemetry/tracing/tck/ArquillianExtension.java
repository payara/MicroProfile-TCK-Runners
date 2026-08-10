/*
 *
 *  DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 *  Copyright (c) 2026 Payara Foundation and/or its affiliates. All rights reserved.
 *
 *  The contents of this file are subject to the terms of either the GNU
 *  General Public License Version 2 only ("GPL") or the Common Development
 *  and Distribution License("CDDL") (collectively, the "License").  You
 *  may not use this file except in compliance with the License.  You can
 *  obtain a copy of the License at
 *  https://github.com/payara/Payara/blob/master/LICENSE.txt
 *  See the License for the specific
 *  language governing permissions and limitations under the License.
 *
 *  When distributing the software, include this License Header Notice in each
 *  file and include the License file at glassfish/legal/LICENSE.txt.
 *
 *  GPL Classpath Exception:
 *  The Payara Foundation designates this particular file as subject to the "Classpath"
 *  exception as provided by the Payara Foundation in the GPL Version 2 section of the License
 *  file that accompanied this code.
 *
 *  Modifications:
 *  If applicable, add the following below the License Header, with the fields
 *  enclosed by brackets [] replaced by your own identifying information:
 *  "Portions Copyright [year] [name of copyright owner]"
 *
 *  Contributor(s):
 *  If you wish your version of this file to be governed by only the CDDL or
 *  only the GPL Version 2, indicate your decision by adding "[Contributor]
 *  elects to include this software in this distribution under the [CDDL or GPL
 *  Version 2] license."  If you don't indicate a single choice of license, a
 *  recipient has the option to distribute your version of this file under
 *  either the CDDL, the GPL Version 2 or to extend the choice of license to
 *  its licensees as provided above.  However, if you add GPL Version 2 code
 *  and therefore, elected the GPL Version 2 license, then the option applies
 *  only if the new code is made subject to such option by the copyright
 *  holder.
 *
 */
package fish.payara.microprofile.telemetry.tracing.tck;

import io.opentelemetry.sdk.autoconfigure.spi.AutoConfigurationCustomizerProvider;
import org.eclipse.microprofile.config.spi.ConfigSource;
import org.jboss.arquillian.container.test.spi.client.deployment.ApplicationArchiveProcessor;
import org.jboss.arquillian.core.spi.LoadableExtension;
import org.jboss.arquillian.test.spi.TestClass;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.Node;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;

/**
 * @author ariekiswanto
 */
public class ArquillianExtension implements LoadableExtension {
    
    private static final String EXECUTOR_PROPERTY = "telemetry.tck.executor";
    private static final String PATH = "META-INF/microprofile-telemetry-tck.properties";
    
    @Override
    public void register(ExtensionBuilder extensionBuilder) {
        extensionBuilder.service(ApplicationArchiveProcessor.class, ApplicationArchiveProcessorImpl.class);
    }

    /**
     * @author ariekiswanto
     */
    public static class ApplicationArchiveProcessorImpl implements ApplicationArchiveProcessor {
        @Override
        public void process(Archive<?> archive, TestClass testClass) {
            WebArchive webArchive = WebArchive.class.cast(archive);
            // json serialization of traces
            // OpenTelemetry setup
            webArchive
                    .addPackages(true, "fish.payara.microprofile.telemetry.tracing.tck")
                    .addAsResource(new StringAsset(EXECUTOR_PROPERTY + "=" + PayaraExecutor.class.getName()), PATH);
            // Drop Payara's concurrent waiting spans so TCK exact-count assertions are unaffected.
            // Append to any existing service file rather than replacing it, so that test-supplied
            // providers (e.g. CustomizerSpiTest's TestCustomizer) are not overwritten.
            appendServiceProvider(webArchive, AutoConfigurationCustomizerProvider.class, ConcurrentSpanFilter.class);
        }

        private static void appendServiceProvider(WebArchive archive, Class<?> serviceType, Class<?> implementation) {
            ArchivePath path = ArchivePaths.create("WEB-INF/classes/META-INF/services/" + serviceType.getName());
            String existing = "";
            Node node = archive.get(path);
            if (node != null && node.getAsset() != null) {
                try (InputStream is = node.getAsset().openStream()) {
                    existing = new String(is.readAllBytes()).trim();
                } catch (IOException e) {
                    throw new RuntimeException("Failed to read existing service file " + path, e);
                }
            }
            String combined = existing.isEmpty()
                    ? implementation.getName()
                    : existing + "\n" + implementation.getName();
            archive.add(new StringAsset(combined), path);
        }
    }

}
