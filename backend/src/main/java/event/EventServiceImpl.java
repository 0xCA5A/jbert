package event;

import event.router.EventRouter;
import event.router.EventRouterType;
import event.router.InitPlaylistByRfidTagSelectTrackByGpi;
import event.router.SimplePlaylistLoad;
import gpio.GpioService;
import mpd.MpdService;
import rfid.RfidService;

import javax.inject.Inject;
import javax.inject.Named;


public class EventServiceImpl implements EventService {
    public static final String EVENT_ROUTER_TYPE_NAME = "eventRouterType";

    // Event sources
    private final RfidService rfidService;
    private final GpioService gpioService;

    // Event sinks
    private final MpdService mpdService;

    private EventRouter eventRouter;

    @Inject
    public EventServiceImpl(@Named(EVENT_ROUTER_TYPE_NAME) EventRouterType eventRouterType, RfidService rfidService, GpioService gpioService, MpdService mpdService) {
        this.rfidService = rfidService;
        this.gpioService = gpioService;
        this.mpdService = mpdService;

        eventRouter = getEventRouter(eventRouterType);
        eventRouter.configure();
    }

    private EventRouter getEventRouter(EventRouterType eventRouterType) {
        switch (eventRouterType) {
            case InitPlaylistByRfidTagSelectTrackByGpi:
                return new InitPlaylistByRfidTagSelectTrackByGpi(rfidService, gpioService, mpdService);
            case SimplePlaylistLoad:
                return new SimplePlaylistLoad(rfidService, gpioService, mpdService);
            default:
                throw new IllegalArgumentException();
        }
    }
}
