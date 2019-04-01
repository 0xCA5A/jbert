import java.io.IOException;
import java.io.InputStream;
import java.util.logging.LogManager;
import java.util.logging.Logger;


final class LogHelper {
    private static final String loggingProps = "logging.properties";

    private LogHelper() {
    }

    static {
        InputStream stream = LogHelper.class.getClassLoader().getResourceAsStream(loggingProps);
        try {
            LogManager.getLogManager().readConfiguration(stream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static Logger getLogger(String name) {
        return Logger.getLogger(name);
    }
}
