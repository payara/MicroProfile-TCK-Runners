package fish.payara.microprofile.ft.tck;

import org.jboss.arquillian.container.test.spi.client.deployment.ApplicationArchiveProcessor;
import org.jboss.arquillian.container.test.spi.client.deployment.AuxiliaryArchiveAppender;
import org.jboss.arquillian.test.spi.TestClass;
import org.jboss.shrinkwrap.api.*;
import org.jboss.shrinkwrap.api.asset.ArchiveAsset;
import org.jboss.shrinkwrap.api.asset.Asset;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ApplicationArchiveProcessorImpl implements ApplicationArchiveProcessor {

    private static final Logger LOG = Logger.getLogger(ApplicationArchiveProcessorImpl.class.getName());

    @Override
    public void process(Archive<?> applicationArchive, TestClass testClass) {
        for(Map.Entry<ArchivePath, Node> content : applicationArchive.getContent().entrySet()) {
            if (content.getValue().getAsset() instanceof ArchiveAsset) {
                ArchiveAsset asset = (ArchiveAsset) content.getValue().getAsset();
                for(Map.Entry<ArchivePath, Node> contentArchive : asset.getArchive().getContent().entrySet()) {
                    if(contentArchive.getKey().get().contains("META-INF/beans.xml")) {
                        LOG.log(Level.INFO, "Virtually augmented content archive \n");
                        JavaArchive javaArchive = JavaArchive.class.cast(asset.getArchive());
                        javaArchive.delete(contentArchive.getKey());
                        javaArchive.addAsManifestResource("beans.xml", "beans.xml");
                    }
                }
            }
        }
    }
}
