package fish.payara.microprofile.healthcheck.test;

import org.jboss.arquillian.container.test.impl.enricher.resource.URIResourceProvider;
import org.jboss.arquillian.core.spi.LoadableExtension;
import org.jboss.arquillian.test.spi.enricher.resource.ResourceProvider;

/**
 *
 * @author Ondrej Mihalyi
 */
public class PayaraExtension implements LoadableExtension {
    @Override
    public void register(LoadableExtension.ExtensionBuilder extensionBuilder) {
        extensionBuilder.override(ResourceProvider.class, URIResourceProvider.class, PayaraHealthUriProvider.class);
    }
}