import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

import java.util.logging.Logger;


class GpiListener implements AutoCloseable {
    private static final Logger logger = Logger.getLogger(GpiListener.class.getName());
    private final GpioController gpioController = GpioFactory.getInstance();
    private final Pin pin;

    GpiListener(Pin pin) {
        this.pin = pin;
    }

    void configure(GpioPinListenerDigital listener) {
        logger.info(String.format("Configure pin '%s' and registering listener", pin));
        GpioPinDigitalInput digitalInput = gpioController.provisionDigitalInputPin(pin, PinPullResistance.PULL_DOWN);
        digitalInput.setShutdownOptions(true);
        digitalInput.addListener(listener);
    }

    @Override
    public void close() {
        gpioController.shutdown();
    }
}
