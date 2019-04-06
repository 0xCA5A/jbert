import com.pi4j.io.gpio.PinEdge;
import com.pi4j.io.gpio.RaspiPin;
import gpio.GpiListener;
import gpio.PlayNextTrackAction;
import gpio.PlayPauseAction;
import gpio.PlayPreviousTrackAction;
import gpio.VolumeDownAction;
import gpio.VolumeUpAction;
import mpd.MpdCommunicator;
import util.LogHelper;
import util.MpcWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


class Jbert {
    private static final Logger logger = LogHelper.getLogger(Jbert.class.getName());

    private final MpdCommunicator mpdCommunicator;
    private final MpcWrapper mpcWrapper;
    private List<GpiListener> gpiListeners;

    private Jbert(String server, int port) {
        mpdCommunicator = new MpdCommunicator(server, port);
        mpcWrapper = new MpcWrapper(server, port);
    }

    private void start() throws InterruptedException {
        logger.info("Starting main jbert application...");

        mpdCommunicator.configure();
        gpiListeners = configureGpiListener();

        loopForever();
    }

    private List<GpiListener> configureGpiListener() {
        List<GpiListener> gpiListenerList = new ArrayList<>();

        GpiListener playPauseGpiListener = new GpiListener(RaspiPin.GPIO_21);
        playPauseGpiListener.registerAction(new PlayPauseAction(PinEdge.RISING, mpdCommunicator));
        gpiListenerList.add(playPauseGpiListener);

        GpiListener volumeUpGpiListener = new GpiListener(RaspiPin.GPIO_03);
        volumeUpGpiListener.registerAction(new VolumeUpAction(PinEdge.RISING, mpcWrapper));
        gpiListenerList.add(volumeUpGpiListener);

        GpiListener volumeDownGpiListener = new GpiListener(RaspiPin.GPIO_05);
        volumeDownGpiListener.registerAction(new VolumeDownAction(PinEdge.RISING, mpcWrapper));
        gpiListenerList.add(volumeDownGpiListener);

        GpiListener playNextTrackGpiListener = new GpiListener(RaspiPin.GPIO_04);
        playNextTrackGpiListener.registerAction(new PlayNextTrackAction(PinEdge.RISING, mpdCommunicator));
        gpiListenerList.add(playNextTrackGpiListener);

        GpiListener playPreviousTrackGpiListener = new GpiListener(RaspiPin.GPIO_27);
        playPreviousTrackGpiListener.registerAction(new PlayPreviousTrackAction(PinEdge.RISING, mpdCommunicator));
        gpiListenerList.add(playPreviousTrackGpiListener);

        return gpiListenerList;
    }

    private void loopForever() throws InterruptedException {
        logger.finer("Enter main loop...");

        while (true) {
            Thread.sleep(1000);
        }
    }

    public static void main(String... args) throws InterruptedException {
        Jbert jbert = new Jbert("localhost", 6600);
        jbert.start();
    }
}
