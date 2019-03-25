import com.pi4j.io.gpio.RaspiPin;

import java.util.logging.Logger;


public class Jbert {
    final static Logger logger = Logger.getLogger(Jbert.class.getName());

    private void start() throws InterruptedException {
        GpiListener gpiListener = new GpiListener();
        gpiListener.configure(RaspiPin.GPIO_02,
                event -> logger.info(" --> GPIO PIN STATE CHANGE: " + event.getPin() + " = " + event.getState()));

        MpdCommunicator mpdTest = new MpdCommunicator("10.0.50.120", 6600);
        mpdTest.configure();

        loopForever();
    }

    private void loopForever() throws InterruptedException {
        while (true) {
            Thread.sleep(500);
        }
    }

    public static void main(String... args) throws InterruptedException {
        Jbert jbert = new Jbert();
        jbert.start();
    }
}
