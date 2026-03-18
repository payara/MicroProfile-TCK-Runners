package fish.payara.microprofile.telemetry.tck;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class CustomLoggerFormatter extends Formatter {
    
    @Override
    public String format(LogRecord record) {
        String message = record.getMessage();
        message += " scopeInfo:full";
        return String.format("[%s] [%s] %s %n",
                record.getMillis(), record.getLevel(), message);
    }
}
