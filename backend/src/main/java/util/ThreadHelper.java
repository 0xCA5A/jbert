package util;

import java.time.Duration;
import java.util.logging.Logger;

public class ThreadHelper {
    private static final Logger logger = LogHelper.getLogger(ThreadHelper.class.getName());

    public static void snooze(Duration duration) {
        try {
            Thread.sleep(duration.toMillis());
        } catch (InterruptedException e) {
            final long threadId = Thread.currentThread().getId();
            final String threadName = Thread.currentThread().getName();
            logger.severe(String.format("Current thread '%s' (id: %d) was interrupted while sleeping: %s", threadName, threadId, e.getMessage()));
        }
    }
}
