package event.router;

import gpio.GpioService;
import gpio.PlayNextTrackAction;
import gpio.PlayPauseAction;
import gpio.VolumeDownAction;
import gpio.VolumeUpAction;
import mpd.MpdService;
import rfid.RfidService;
import rfid.RfidTagUid;

import javax.inject.Inject;
import java.util.Map;

import static hal.JbertControlButton.YELLOW_FRONT_LEFT;
import static hal.JbertControlButton.YELLOW_FRONT_RIGHT;
import static hal.JbertControlButton.YELLOW_TOP_LEFT;
import static hal.JbertControlButton.YELLOW_TOP_RIGHT;


public class EventRouterUtil {
    private final RfidService rfidService;
    private final MpdService mpdService;

    @Inject
    public EventRouterUtil(RfidService rfidService, MpdService mpdService) {
        this.rfidService = rfidService;
        this.mpdService = mpdService;
    }

    public void configureControlGpiListener(GpioService gpioService, MpdService mpdService) {
        gpioService.registerGpiListener(YELLOW_FRONT_RIGHT.getGpioName(), new VolumeDownAction(mpdService));
        gpioService.registerGpiListener(YELLOW_FRONT_LEFT.getGpioName(), new VolumeUpAction(mpdService));
        gpioService.registerGpiListener(YELLOW_TOP_LEFT.getGpioName(), new PlayPauseAction(mpdService));
        gpioService.registerGpiListener(YELLOW_TOP_RIGHT.getGpioName(), new PlayNextTrackAction(mpdService));
    }

    public void registerPlaylistChangeActions(Map<RfidTagUid, String> rfidTagMapping, boolean autoPlayback) {
        rfidTagMapping.forEach((rfidTagUid, playlistName) -> {
            final PlaylistChangeAction playListChangeAction = new PlaylistChangeAction(mpdService, rfidTagUid, playlistName, autoPlayback);
            rfidService.addListener(playListChangeAction);
        });
    }
}
