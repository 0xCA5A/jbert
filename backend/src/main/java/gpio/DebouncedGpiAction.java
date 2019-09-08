package gpio;

import com.pi4j.io.gpio.PinEdge;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import utils.LogHelper;

import java.time.Duration;
import java.time.Instant;
import java.util.logging.Logger;


public abstract class DebouncedGpiAction implements GpioPinListenerDigital {
    private static final Logger logger = LogHelper.getLogger(DebouncedGpiAction.class.getName());

    private final Duration MIN_EVENT_INTERVAL = Duration.ofMillis(300);
    private final PinEdge eventTriggerEdge;
    private Instant lastEventActiveTimestamp;

    DebouncedGpiAction(PinEdge edge) {
        this.eventTriggerEdge = edge;
        this.lastEventActiveTimestamp = Instant.now().minusNanos(MIN_EVENT_INTERVAL.toNanos() * 2);
    }

    private void logPinState(GpioPinDigitalStateChangeEvent event) {
        logger.fine(String.format("GPI pin state change event received: %s = %s (%s)", event.getPin(), event.getState(), event.getEdge()));
    }

    private boolean isEventConsumed() {
        Instant now = Instant.now();
        if (lastEventActiveTimestamp.plus(MIN_EVENT_INTERVAL).isBefore(now)) {
            lastEventActiveTimestamp = now;
            return true;
        }

        final long millisDelta = lastEventActiveTimestamp.plus(MIN_EVENT_INTERVAL).toEpochMilli() - now.toEpochMilli();
        logger.finer(String.format("Ignore '%s' event for %d more ms", eventTriggerEdge, millisDelta));
        return false;
    }

    @Override
    public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
        logPinState(event);

        if (event.getEdge() == eventTriggerEdge && isEventConsumed()) {
            logger.fine(String.format("Call registered action callback on valid pin '%s' event", event.getPin()));
            gpiEventAction(event);
        }
    }

    protected abstract void gpiEventAction(GpioPinDigitalStateChangeEvent event);
}

