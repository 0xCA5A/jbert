package event.router;

import gpio.GpioService;
import gpio.PlayTrackByIndexAction;
import hal.JbertPlaylistSelectButtonName;
import mpd.MpdService;
import rfid.RfidService;
import rfid.RfidTagUid;

import javax.inject.Inject;


/**
 * Event router implementation loading playlists by GPI and RFID event
 */
public class InitPlaylistByRfidTagSelectTrackByGpi implements EventRouter {
    // Event sources
    private final RfidService rfidService;
    private final GpioService gpioService;

    // Event sinks
    private final MpdService mpdService;

    @Inject
    public InitPlaylistByRfidTagSelectTrackByGpi(RfidService rfidService, GpioService gpioService, MpdService mpdService) {
        this.rfidService = rfidService;
        this.gpioService = gpioService;
        this.mpdService = mpdService;
    }

    @Override
    public void configure() {
        registerPlaylistChangeActions();

        EventRouterUtil.configureControlGpiListener(gpioService, mpdService);
        configureSongSelectGpiListener();
    }

    private void registerPlaylistChangeActions() {
        registerPlaylistChangeAction(new RfidTagUid("55-68-00-D2-EF"), "animals");
        registerPlaylistChangeAction(new RfidTagUid("FA-00-9C-73-15"), "argentina-hits");
        registerPlaylistChangeAction(new RfidTagUid("E5-EA-00-D2-DD"), "arg-sleeping");
        registerPlaylistChangeAction(new RfidTagUid("F5-BA-4E-D3-D2"), "valen-hits");
        registerPlaylistChangeAction(new RfidTagUid("F5-82-FE-D1-58"), "arg-children-songs");
        registerPlaylistChangeAction(new RfidTagUid("FF-FF-FF-FF-72"), "chaschperli");
        registerPlaylistChangeAction(new RfidTagUid("FF-FF-FF-FF-FA"), "xmas-party");
        registerPlaylistChangeAction(new RfidTagUid("FF-FF-FF-FF-FB"), "ch-children-songs");
        registerPlaylistChangeAction(new RfidTagUid("FF-FF-FF-FF-FC"), "ch-sleeping");
    }

    private void registerPlaylistChangeAction(RfidTagUid rfidTagUid, String playlist) {
        PlaylistChangeAction playListChangeAction = new PlaylistChangeAction(mpdService, rfidTagUid, playlist, true);
        rfidService.addListener(playListChangeAction);
    }

    private void configureSongSelectGpiListener() {
        gpioService.registerGpiListener(JbertPlaylistSelectButtonName.RED,
                new PlayTrackByIndexAction(mpdService, 0));
        gpioService.registerGpiListener(JbertPlaylistSelectButtonName.BLACK,
                new PlayTrackByIndexAction(mpdService, 1));
        gpioService.registerGpiListener(JbertPlaylistSelectButtonName.GREEN,
                new PlayTrackByIndexAction(mpdService, 2));
        gpioService.registerGpiListener(JbertPlaylistSelectButtonName.BLUE,
                new PlayTrackByIndexAction(mpdService, 3));
        gpioService.registerGpiListener(JbertPlaylistSelectButtonName.YELLOW,
                new PlayTrackByIndexAction(mpdService, 4));
    }
}
