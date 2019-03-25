import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

import java.util.logging.Logger;

public class GpiListener implements AutoCloseable {
    private static final Logger logger = Logger.getLogger(GpiListener.class.getName());

    // Create GPIO controller
    final GpioController gpio = GpioFactory.getInstance();

    private GpioPinDigitalInput button;

    GpiListener() {
    }

    @Override
    public void close() {
        gpio.shutdown();
    }

    public void configure(Pin pin, GpioPinListenerDigital listener) {
        logger.info(String.format("Configure pin '%s' and registering listener '%s'", pin, listener));

        // Provision GPIO pin 02 as an input pin with its internal pull down resistor enabled
        button = gpio.provisionDigitalInputPin(RaspiPin.GPIO_02, PinPullResistance.PULL_DOWN);
        // Set shutdown state for this input pin
        button.setShutdownOptions(true);

        // Create and register GPIO pin listener
        button.addListener(listener);
    }

    public void shutdown() {
        gpio.shutdown();
    }

}
