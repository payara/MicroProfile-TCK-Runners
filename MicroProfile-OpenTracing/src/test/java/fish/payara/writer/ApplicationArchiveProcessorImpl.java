package fish.payara.writer;

import org.glassfish.jersey.internal.spi.AutoDiscoverable;
import org.jboss.arquillian.container.test.spi.client.deployment.ApplicationArchiveProcessor;
import org.jboss.arquillian.test.spi.TestClass;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;

public class ApplicationArchiveProcessorImpl implements ApplicationArchiveProcessor {
    @Override
    public void process(Archive<?> archive, TestClass testClass) {
        WebArchive webArchive = WebArchive.class.cast(archive);
        webArchive.addClass(AutoDiscoverableImpl.class)
                .addClass(MessageBodyWriterProvider.class)
                .addAsServiceProvider(AutoDiscoverable.class, AutoDiscoverableImpl.class);
    }
}
