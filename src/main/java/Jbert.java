import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinEdge;
import com.pi4j.io.gpio.RaspiPin;
import gpio.DebouncedGpiAction;
import gpio.GpiListener;
import gpio.PlayNextTrackAction;
import gpio.PlayPauseAction;
import gpio.PlayPreviousTrackAction;
import gpio.VolumeDownAction;
import gpio.VolumeUpAction;
import mpd.MpdCommunicator;
import rfid.PlaylistChangeAction;
import rfid.RfidTagDetectionListener;
import rfid.RfidTagDetector;
import util.LogHelper;
import util.MpcWrapper;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.logging.Logger;


class Jbert {
    private static final Logger logger = LogHelper.getLogger(Jbert.class.getName());

    private final MpdCommunicator mpdCommunicator;
    private final MpcWrapper mpcWrapper;
    private final RfidTagDetector rfidTagDetector;

    private List<GpiListener> gpiListeners;
    private List<RfidTagDetectionListener> rfidListeners;


    private Jbert(String server, int port) {
        mpdCommunicator = new MpdCommunicator(server, port);
        mpcWrapper = new MpcWrapper(server, port);
        rfidTagDetector = new RfidTagDetector();
    }

    public static void main(String... args) throws InterruptedException {
        Jbert jbert = new Jbert("localhost", 6600);
        jbert.start();
    }

    private void start() throws InterruptedException {
        logger.info("Starting main jbert application...");

        mpdCommunicator.configure();

        gpiListeners = configureGpiListener();

        rfidTagDetector.configure(Duration.ofSeconds(2));
        rfidTagDetector.start(Executors.newSingleThreadExecutor());

        rfidListeners = configureRfidListener();

        loopForever();
    }

    private List<RfidTagDetectionListener> configureRfidListener() {
        List<RfidTagDetectionListener> rfidListeners = new ArrayList<>();

        PlaylistChangeAction playListChangeAction = new PlaylistChangeAction(mpdCommunicator);
        rfidListeners.add(playListChangeAction);

        // Add all created listeners to the RfidTagDetector object
        for (RfidTagDetectionListener rfidListener : rfidListeners) {
            rfidTagDetector.addListener(rfidListener);
        }
        return rfidListeners;
    }

    private List<GpiListener> configureGpiListener() {
        List<GpiListener> gpiListenerList = new ArrayList<>();
        registerGpiListener(RaspiPin.GPIO_21,
                new PlayPauseAction(PinEdge.RISING, mpdCommunicator), gpiListenerList);
        registerGpiListener(RaspiPin.GPIO_03,
                new VolumeUpAction(PinEdge.RISING, mpcWrapper), gpiListenerList);
        registerGpiListener(RaspiPin.GPIO_05,
                new VolumeDownAction(PinEdge.RISING, mpcWrapper), gpiListenerList);
        registerGpiListener(RaspiPin.GPIO_27,
                new PlayNextTrackAction(PinEdge.RISING, mpdCommunicator), gpiListenerList);
        registerGpiListener(RaspiPin.GPIO_04,
                new PlayPreviousTrackAction(PinEdge.RISING, mpdCommunicator), gpiListenerList);
        return gpiListenerList;
    }

    private void registerGpiListener(Pin pin, DebouncedGpiAction action, List<GpiListener> gpiListenerList) {
        GpiListener gpiListener = new GpiListener(pin);
        gpiListener.registerAction(action);
        gpiListenerList.add(gpiListener);
    }

    private void loopForever() throws InterruptedException {
        logger.finer("Enter main loop...");

        while (true) {
            Thread.sleep(1000);
        }
    }
}
