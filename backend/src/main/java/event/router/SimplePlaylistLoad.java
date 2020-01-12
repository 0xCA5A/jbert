package event.router;

import gpio.GpioService;
import gpio.PlaylistLoadAction;
import mpd.MpdService;
import rfid.RfidService;
import rfid.RfidTagUid;

import javax.inject.Inject;

import static hal.JbertPlaylistSelectButtonName.BLACK;
import static hal.JbertPlaylistSelectButtonName.BLUE;
import static hal.JbertPlaylistSelectButtonName.GREEN;
import static hal.JbertPlaylistSelectButtonName.RED;
import static hal.JbertPlaylistSelectButtonName.YELLOW;


/**
 * Event router implementation loading playlists by GPI and RFID event
 */
public class SimplePlaylistLoad implements EventRouter {
    // Event sources
    private final RfidService rfidService;
    private final GpioService gpioService;

    // Event sinks
    private final MpdService mpdService;

    @Inject
    public SimplePlaylistLoad(RfidService rfidService, GpioService gpioService, MpdService mpdService) {
        this.rfidService = rfidService;
        this.gpioService = gpioService;
        this.mpdService = mpdService;
    }

    @Override
    public void configure() {
        configureRfidListener();

        EventRouterUtil.configureControlGpiListener(gpioService, mpdService);
        configurePlaylistSelectGpiListener();
    }

    private void configureRfidListener() {
        registerRfidServiceListener(new RfidTagUid("55-68-00-D2-EF"), "red");
        registerRfidServiceListener(new RfidTagUid("FA-00-9C-73-15"), "black");
        registerRfidServiceListener(new RfidTagUid("E5-EA-00-D2-DD"), "green");
        registerRfidServiceListener(new RfidTagUid("F5-BA-4E-D3-D2"), "blue");
        registerRfidServiceListener(new RfidTagUid("F5-82-FE-D1-58"), "yellow");
    }

    private void registerRfidServiceListener(RfidTagUid rfidTagUid, String playlist) {
        PlaylistChangeAction playListChangeAction = new PlaylistChangeAction(mpdService, rfidTagUid, playlist, true);
        rfidService.addListener(playListChangeAction);
    }

    private void configurePlaylistSelectGpiListener() {
        gpioService.registerGpiListener(RED, new PlaylistLoadAction(mpdService, "red"));
        gpioService.registerGpiListener(BLACK, new PlaylistLoadAction(mpdService, "black"));
        gpioService.registerGpiListener(GREEN, new PlaylistLoadAction(mpdService, "green"));
        gpioService.registerGpiListener(BLUE, new PlaylistLoadAction(mpdService, "blue"));
        gpioService.registerGpiListener(YELLOW, new PlaylistLoadAction(mpdService, "yellow"));
    }
}
