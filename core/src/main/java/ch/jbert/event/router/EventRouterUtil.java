package ch.jbert.event.router;

import ch.jbert.gpio.GpioService;
import ch.jbert.gpio.PlayNextTrackAction;
import ch.jbert.gpio.PlayPauseAction;
import ch.jbert.gpio.VolumeDownAction;
import ch.jbert.gpio.VolumeUpAction;
import ch.jbert.mpd.MpdService;
import ch.jbert.rfid.RfidService;
import ch.jbert.rfid.RfidServiceImpl;
import ch.jbert.rfid.RfidTagUid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.util.Map;

import static ch.jbert.hal.JbertControlButton.YELLOW_FRONT_LEFT;
import static ch.jbert.hal.JbertControlButton.YELLOW_FRONT_RIGHT;
import static ch.jbert.hal.JbertControlButton.YELLOW_TOP_LEFT;
import static ch.jbert.hal.JbertControlButton.YELLOW_TOP_RIGHT;

@Singleton
public class EventRouterUtil {
    private static final Logger logger = LoggerFactory.getLogger(RfidServiceImpl.class);

    private final RfidService rfidService;
    private final MpdService mpdService;

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
            logger.debug("Register playlist change action for RFID tag UID '{}' and playlist name '{}'", rfidTagUid, playlistName);
            final PlaylistChangeAction playListChangeAction = new PlaylistChangeAction(mpdService, rfidTagUid, playlistName, autoPlayback);
            rfidService.addListener(playListChangeAction);
        });
    }
}
