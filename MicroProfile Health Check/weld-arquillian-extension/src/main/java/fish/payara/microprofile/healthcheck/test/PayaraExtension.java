package fish.payara.microprofile.healthcheck.test;

import org.jboss.arquillian.core.spi.LoadableExtension;
import org.jboss.arquillian.test.spi.enricher.resource.ResourceProvider;

/**
 *
 * @author Ondrej Mihalyi
 */
public class PayaraExtension implements LoadableExtension {
    @Override
    public void register(LoadableExtension.ExtensionBuilder extensionBuilder) {
        extensionBuilder.service(ResourceProvider.class, PayaraHealthUriProvider.class);
    }
}