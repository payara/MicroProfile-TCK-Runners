package fish.payara.writer;

import jakarta.ws.rs.core.Feature;
import jakarta.ws.rs.core.FeatureContext;
import org.glassfish.jersey.internal.spi.AutoDiscoverable;

public class AutoDiscoverableImpl implements AutoDiscoverable {

    @Override
    public void configure(FeatureContext featureContext) {
        featureContext.register(MessageBodyWriterProvider.class);
    }

}
