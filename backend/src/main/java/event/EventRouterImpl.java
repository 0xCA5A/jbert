package event;

import gpio.GpioService;
import gpio.PlayNextTrackAction;
import gpio.PlayPauseAction;
import gpio.PlayPreviousTrackAction;
import gpio.VolumeDownAction;
import gpio.VolumeUpAction;
import mpd.MpdService;
import rfid.RfidService;
import rfid.RfidTagDetectionListener;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;


public class EventRouterImpl implements EventRouter {
    // Event sources
    private final RfidService rfidService;
    private final GpioService gpioService;

    // Event sinks
    private final MpdService mpdService;

    @Inject
    EventRouterImpl(RfidService rfidService, GpioService gpioService, MpdService mpdService) {
        this.rfidService = rfidService;
        this.gpioService = gpioService;
        this.mpdService = mpdService;

        configureGpiListener();
        configureRfidListener();
    }

    private void configureGpiListener() {
        gpioService.registerGpiListener("GPIO 21", new PlayPauseAction(mpdService));
        gpioService.registerGpiListener("GPIO 3", new VolumeUpAction(mpdService));
        gpioService.registerGpiListener("GPIO 5", new VolumeDownAction(mpdService));
        gpioService.registerGpiListener("GPIO 27", new PlayNextTrackAction(mpdService));
        gpioService.registerGpiListener("GPIO 4", new PlayPreviousTrackAction(mpdService));
    }

    private void configureRfidListener() {
        List<RfidTagDetectionListener> rfidListeners = new ArrayList<>();

        PlaylistChangeAction playListChangeAction = new PlaylistChangeAction(mpdService);
        rfidListeners.add(playListChangeAction);

        // Add all created listeners to the RfidTagDetector object
        for (RfidTagDetectionListener rfidListener : rfidListeners) {
            rfidService.addListener(rfidListener);
        }
    }
}
