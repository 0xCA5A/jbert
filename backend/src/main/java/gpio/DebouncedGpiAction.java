package gpio;

import com.pi4j.io.gpio.PinEdge;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;


public abstract class DebouncedGpiAction implements GpioPinListenerDigital {
    private static final Logger logger = LoggerFactory.getLogger(DebouncedGpiAction.class);

    private final Duration MIN_EVENT_INTERVAL = Duration.ofMillis(300);
    private final PinEdge eventTriggerEdge;
    private Instant lastEventActiveTimestamp;

    DebouncedGpiAction(PinEdge edge) {
        this.eventTriggerEdge = edge;
        this.lastEventActiveTimestamp = Instant.now().minusNanos(MIN_EVENT_INTERVAL.toNanos() * 2);
    }

    private void logPinState(GpioPinDigitalStateChangeEvent event) {
        logger.debug("GPI pin state change event received: {} = {} ({})", event.getPin(), event.getState(), event.getEdge());
    }

    private boolean isEventConsumed() {
        Instant now = Instant.now();
        if (lastEventActiveTimestamp.plus(MIN_EVENT_INTERVAL).isBefore(now)) {
            lastEventActiveTimestamp = now;
            return true;
        }

        final long millisDelta = lastEventActiveTimestamp.plus(MIN_EVENT_INTERVAL).toEpochMilli() - now.toEpochMilli();
        logger.trace("Ignore '{}' event for {} more ms", eventTriggerEdge, millisDelta);
        return false;
    }

    @Override
    public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
        logPinState(event);

        if (event.getEdge() == eventTriggerEdge && isEventConsumed()) {
            logger.debug("Call registered action callback on valid pin '{}' event", event.getPin());
            gpiEventAction(event);
        }
    }

    protected abstract void gpiEventAction(GpioPinDigitalStateChangeEvent event);
}

