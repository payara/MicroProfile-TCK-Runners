package fish.payara.microprofile.config.test;

import org.jboss.arquillian.container.test.spi.client.deployment.ApplicationArchiveProcessor;
import org.jboss.arquillian.test.spi.TestClass;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;

/**
 *
 * @author Ondrej Mihalyi
 */
public class PayaraArchiveProcessor implements ApplicationArchiveProcessor {

    @Override
    public void process(Archive<?> applicationArchive, TestClass testClass) {
        if (applicationArchive instanceof WebArchive) {
            JavaArchive configJar = ShrinkWrap
                    .create(JavaArchive.class, "payara-config-impl.jar");
            
            // add files of the implementation into the configJar archive, or read the contents of the configJar archive from a JAR file or from maven depdendency with the maven resolver
                    
            ((WebArchive) applicationArchive).addAsLibraries(configJar);
        }
    }
}