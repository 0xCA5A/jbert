package event;

import event.router.EventRouter;
import event.router.InitPlaylistByRfidTagSelectTrackByGpi;
import event.router.SimplePlaylistLoad;
import gpio.GpioService;
import mpd.MpdService;
import rfid.RfidService;
import rfid.RfidTagUid;

import javax.inject.Inject;
import java.util.Map;


public class EventServiceImpl implements EventService {
    // Event sources
    private final RfidService rfidService;
    private final GpioService gpioService;

    // Event sinks
    private final MpdService mpdService;

    private EventRouter eventRouter;

    @Inject
    public EventServiceImpl(EventServiceConfig eventServiceConfig, RfidService rfidService, GpioService gpioService, MpdService mpdService) {
        this.rfidService = rfidService;
        this.gpioService = gpioService;
        this.mpdService = mpdService;

        this.eventRouter = getEventRouterInstance(eventServiceConfig);
        this.eventRouter.configure();
    }

    private EventRouter getEventRouterInstance(EventServiceConfig eventServiceConfig) {
        final Map<RfidTagUid, String> rfidTagMapping = eventServiceConfig.getRfidTagMapping();

        switch (eventServiceConfig.getEventRouterType()) {
            case InitPlaylistByRfidTagSelectTrackByGpi:
                return new InitPlaylistByRfidTagSelectTrackByGpi(rfidTagMapping, rfidService, gpioService, mpdService);
            case SimplePlaylistLoad:
                return new SimplePlaylistLoad(rfidTagMapping, rfidService, gpioService, mpdService);
            default:
                throw new IllegalArgumentException();
        }
    }
}
