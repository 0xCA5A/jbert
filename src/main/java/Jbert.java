import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


class PlayPauseAction implements GpioPinListenerDigital {
    private final static Logger logger = Logger.getLogger(PlayPauseAction.class.getName());

    private final MpdCommunicator mpdCommunicator;
    private boolean playbackActive;

    PlayPauseAction(MpdCommunicator mpdCommunicator) {
        this.mpdCommunicator = mpdCommunicator;
        playbackActive = false;
    }

    @Override
    public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
        logger.info(" --> GPIO PIN STATE CHANGE: " + event.getPin() + " = " + event.getState());

        if (event.getState() == PinState.HIGH) {
            return;
        }

        if (playbackActive) {
            mpdCommunicator.pause();
            playbackActive = false;
        } else {
            mpdCommunicator.play();
            playbackActive = true;
        }
    }
}

class VolumeUpAction implements GpioPinListenerDigital {
    private final static Logger logger = Logger.getLogger(VolumeUpAction.class.getName());

    private final MpdCommunicator mpdCommunicator;

    VolumeUpAction(MpdCommunicator mpdCommunicator) {
        this.mpdCommunicator = mpdCommunicator;
    }

    @Override
    public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
        logger.info(" --> GPIO PIN STATE CHANGE: " + event.getPin() + " = " + event.getState());
        mpdCommunicator.increaseVolume();
    }
}

class VolumeDownAction implements GpioPinListenerDigital {
    private final static Logger logger = Logger.getLogger(VolumeDownAction.class.getName());

    private final MpdCommunicator mpdCommunicator;

    VolumeDownAction(MpdCommunicator mpdCommunicator) {
        this.mpdCommunicator = mpdCommunicator;
    }

    @Override
    public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
        logger.info(" --> GPIO PIN STATE CHANGE: " + event.getPin() + " = " + event.getState());
        mpdCommunicator.decreaseVolume();
    }
}

class Jbert {
    private final static Logger logger = Logger.getLogger(Jbert.class.getName());

    private final List<GpiListener> gpiListenerList = new ArrayList<>();
    private final MpdCommunicator mpdCommunicator;

    private Jbert(String server, int port) {
        mpdCommunicator = new MpdCommunicator(server, port);
    }

    private void configure() {
        logger.info("Starting the jbert application");

        mpdCommunicator.configure();

        registerGpiListener(RaspiPin.GPIO_04, new PlayPauseAction(mpdCommunicator));
        registerGpiListener(RaspiPin.GPIO_05, new VolumeUpAction(mpdCommunicator));
        registerGpiListener(RaspiPin.GPIO_06, new VolumeDownAction(mpdCommunicator));
    }

    private void registerGpiListener(Pin pin, GpioPinListenerDigital action) {
        GpiListener gpiListener = new GpiListener(pin);
        gpiListener.configure(action);
        gpiListenerList.add(gpiListener);
    }

    private void start() throws InterruptedException {
        logger.info("Starting the jbert application...");
        loopForever();
    }

    private void loopForever() throws InterruptedException {
        while (true) {
            Thread.sleep(500);
        }
    }

    public static void main(String... args) throws InterruptedException {
        Jbert jbert = new Jbert("localhost", 6600);
        jbert.configure();
        jbert.start();
    }
}
