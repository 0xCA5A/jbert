package event.router;

import gpio.GpioService;
import gpio.PlaylistLoadAction;
import hal.JbertPlaylistSelectButton;
import mpd.MpdService;
import rfid.RfidService;
import rfid.RfidTagUid;

import java.util.EnumSet;
import java.util.Map;


/**
 * Event router implementation loading playlists by GPI and RFID events
 * <p>
 * Loads and plays playlists
 * <ul>
 *     <li>on button events named by JbertPlaylistSelectButton event elements (lower case)</li>
 *     <li>on RFID tag detection events, mapping defined in the application configuration</li>
 * </ul>
 */
public class SimplePlaylistLoad implements EventRouter {
    private final Map<RfidTagUid, String> rfidTagMapping;
    private final EventRouterUtil eventRouterUtil;

    // Event sources
    private final GpioService gpioService;

    // Event sinks
    private final MpdService mpdService;

    public SimplePlaylistLoad(Map<RfidTagUid, String> rfidTagMapping, RfidService rfidService, GpioService gpioService, MpdService mpdService) {
        this.rfidTagMapping = rfidTagMapping;
        this.eventRouterUtil = new EventRouterUtil(rfidService, mpdService);
        this.gpioService = gpioService;
        this.mpdService = mpdService;
    }

    @Override
    public void configure() {
        eventRouterUtil.registerPlaylistChangeActions(rfidTagMapping, true);
        eventRouterUtil.configureControlGpiListener(gpioService, mpdService);

        configureGpioEventHandling();
    }

    private void configureGpioEventHandling() {
        EnumSet.allOf(JbertPlaylistSelectButton.class).forEach((e) -> gpioService.registerGpiListener(e.getGpioName(),
                new PlaylistLoadAction(mpdService, e.toString().toLowerCase())));
    }
}
