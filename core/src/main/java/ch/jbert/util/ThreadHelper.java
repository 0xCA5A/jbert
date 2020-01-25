package ch.jbert.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;


public class ThreadHelper {
    private static final Logger logger = LoggerFactory.getLogger(ThreadHelper.class);

    public static void snooze(Duration duration) {
        try {
            Thread.sleep(duration.toMillis());
        } catch (InterruptedException e) {
            final long threadId = Thread.currentThread().getId();
            final String threadName = Thread.currentThread().getName();
            logger.error("Current thread '{}' (id: {}) was interrupted while sleeping: {}", threadName, threadId, e.getMessage());
        }
    }
}
