package fish.payara.microprofile.metrics.tck;

import org.jboss.arquillian.container.test.spi.client.deployment.ApplicationArchiveProcessor;
import org.jboss.arquillian.test.spi.TestClass;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;

public class ApplicationArchiveProcessorImpl implements ApplicationArchiveProcessor {
    @Override
    public void process(Archive<?> archive, TestClass testClass) {
        if (!(archive instanceof WebArchive)) {
            return;
        }
        WebArchive webArchive = WebArchive.class.cast(archive);
        webArchive.addAsWebInfResource("beans.xml", "beans.xml");
    }
}
