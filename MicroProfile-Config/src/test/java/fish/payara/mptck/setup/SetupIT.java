package fish.payara.mptck.setup;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.testng.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.testng.Assert;
import org.testng.annotations.Test;

public class SetupIT extends Arquillian {

    @Deployment
    public static WebArchive empty() {
        return ShrinkWrap.create(WebArchive.class); // extensions will do the useful work
    }

    @Test
    public void checkSetup() {
        // check whether the environment variables were populated by the executor correctly
        // we run this as surefire test, so it executes before the TCK and overrides env by means of
        // Arquillian extension environment-setup
        if (!"45".equals(System.getenv("config_ordinal"))) {
            Assert.fail(
                    "Before running this test, the environment variable \"config_ordinal\" must be set with the value of 45");
        }
    }
}
