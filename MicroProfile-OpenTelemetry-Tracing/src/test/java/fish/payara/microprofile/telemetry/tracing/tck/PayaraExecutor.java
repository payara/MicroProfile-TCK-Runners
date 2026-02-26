package fish.payara.microprofile.telemetry.tracing.tck;

import jakarta.enterprise.concurrent.ManagedExecutorService;
import java.util.concurrent.Executor;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class PayaraExecutor implements Executor {

    public static final Logger logger = Logger.getLogger(PayaraExecutor.class.getName());

    @Override
    public void execute(Runnable command) {
        InitialContext ctx = null;
        try {
            ctx = new InitialContext();
            ManagedExecutorService managedExecutorService = (ManagedExecutorService) ctx.lookup("java:comp/DefaultManagedExecutorService");
            managedExecutorService.execute(command);
        } catch (NamingException e) {
            logger.severe("Exception thrown by trying to get resource" + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
