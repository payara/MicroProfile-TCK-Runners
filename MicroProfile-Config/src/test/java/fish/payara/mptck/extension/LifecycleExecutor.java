/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2022 Payara Foundation and/or its affiliates. All rights reserved.
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

import org.jboss.arquillian.container.spi.event.container.BeforeDeploy;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.test.spi.TestClass;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.Node;
import org.jboss.shrinkwrap.api.asset.ArchiveAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author ariekiswanto
 *
 */
public class LifecycleExecutor {

    private static final Logger LOG = Logger.getLogger(LifecycleExecutor.class.getName());

    /**
     * Observes <code>BeforeDeploy</code> event to modify beans definition
     * before deployment happen to prevent CDI deployment failure
     * @param event
     * @param testClass
     */
    public void executeBeforeDeploy(@Observes BeforeDeploy event, TestClass testClass) {
       LOG.info("before deploy event: " + event.getDeployment().getArchive().getName());
        Archive archive = event.getDeployment().getArchive();
        if (archive instanceof WebArchive) {
            WebArchive webArchive = WebArchive.class.cast(archive);
            if (webArchive.contains("WEB-INF/beans.xml")) {
                LOG.info("modify beans");
                webArchive.addAsWebInfResource("beans.xml", "beans.xml");
            }

            for(Map.Entry<ArchivePath, Node> content : webArchive.getContent().entrySet()) {
                if (content.getValue().getAsset() instanceof ArchiveAsset) {
                    ArchiveAsset asset = (ArchiveAsset) content.getValue().getAsset();
                    if (asset.getArchive().contains("META-INF/beans.xml")) {
                        LOG.log(Level.INFO, "Virtually augmented content archive \n");
                        JavaArchive javaArchive = JavaArchive.class.cast(asset.getArchive());
                        javaArchive.addAsManifestResource("beans.xml");

                    }
                }
            }
        }
    }
}
