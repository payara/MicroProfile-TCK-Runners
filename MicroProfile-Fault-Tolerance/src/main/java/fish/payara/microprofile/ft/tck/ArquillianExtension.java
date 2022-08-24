package fish.payara.microprofile.ft.tck;

import org.jboss.arquillian.container.test.spi.client.deployment.ApplicationArchiveProcessor;
import org.jboss.arquillian.container.test.spi.client.deployment.AuxiliaryArchiveAppender;
import org.jboss.arquillian.core.spi.LoadableExtension;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ArquillianExtension implements LoadableExtension {

    private static final Logger LOG = Logger.getLogger(ArquillianExtension.class.getName());

    @Override
    public void register(ExtensionBuilder extensionBuilder) {
        LOG.log(Level.INFO, "\n Registered Payara TCK ArquillianExtension \n");
        extensionBuilder.service(ApplicationArchiveProcessor.class, ApplicationArchiveProcessorImpl.class);
    }
}
