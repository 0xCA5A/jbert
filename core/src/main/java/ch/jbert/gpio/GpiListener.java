package ch.jbert.gpio;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class GpiListener implements AutoCloseable {
    private static final Logger logger = LoggerFactory.getLogger(GpiListener.class);
    private final GpioController gpioController = GpioFactory.getInstance();
    private final GpioPinDigitalInput digitalInput;
    private final Pin pin;

    public GpiListener(Pin pin) {
        logger.trace("Configure pin '{}' as input", pin);
        this.pin = pin;
        digitalInput = gpioController.provisionDigitalInputPin(pin, PinPullResistance.PULL_DOWN);
        digitalInput.setShutdownOptions(true);
    }

    public void registerAction(GpioPinListenerDigital listener) {
        logger.debug("Registering listener '{}' for pin '{}'", listener, pin);
        digitalInput.addListener(listener);
    }

    @Override
    public void close() {
        gpioController.shutdown();
    }
}
