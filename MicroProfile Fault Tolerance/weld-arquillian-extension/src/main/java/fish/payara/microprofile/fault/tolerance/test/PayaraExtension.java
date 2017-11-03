package fish.payara.microprofile.fault.tolerance.test;

import org.jboss.arquillian.container.test.spi.client.deployment.ApplicationArchiveProcessor;
import org.jboss.arquillian.core.spi.LoadableExtension;

/**
 *
 * @author Ondrej Mihalyi
 */
public class PayaraExtension implements LoadableExtension {
    @Override
    public void register(LoadableExtension.ExtensionBuilder extensionBuilder) {
        extensionBuilder.service(ApplicationArchiveProcessor.class, PayaraArchiveProcessor.class);
    }
}