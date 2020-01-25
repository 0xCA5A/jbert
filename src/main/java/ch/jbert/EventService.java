package ch.jbert;

import ch.jbert.event.router.EventRouter;
import ch.jbert.event.router.EventRouterType;
import ch.jbert.event.router.InitPlaylistByRfidTagSelectTrackByGpi;
import ch.jbert.event.router.SimplePlaylistLoad;
import ch.jbert.gpio.GpioService;
import ch.jbert.gpio.GpioServiceImpl;
import ch.jbert.gpio.GpioServiceMockImpl;
import ch.jbert.mpd.MpdService;
import ch.jbert.mpd.MpdServiceImpl;
import ch.jbert.rfid.RfidService;
import ch.jbert.rfid.RfidServiceImpl;
import ch.jbert.rfid.RfidServiceMockImpl;
import ch.jbert.rfid.RfidTagUid;
import io.micronaut.context.annotation.Property;
import io.micronaut.scheduling.annotation.Scheduled;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;
import java.util.Map;
import java.util.stream.Collectors;


@Singleton
public class EventService {
    // Event sources
    private RfidService rfidService;
    private GpioService gpioService;

    // Event sinks
    private MpdService mpdService;

    private EventRouter eventRouter;

    @Property(name = "application.event-router-name")
    private String eventRouterName;

    @Property(name = "application.rfid-tag-mapping")
    private Map<String, String> rfidTagMapping;

    @Property(name = "core.hal.mock.enabled")
    private boolean halMockEnabled;

    @Property(name = "core.mpd.server")
    private String mpdServerName;

    @Property(name = "core.mpd.port")
    private int mpdServerPort;

    @PostConstruct
    public void initialize() {
        this.rfidService = createRfidServiceInstance();
        this.gpioService = createGpioServiceInstance();

        this.mpdService = createMpdService();
        this.mpdService.configure();
        this.mpdService.ensureConnection();

        this.eventRouter = createEventRouterInstance();
        this.eventRouter.configure();
    }

    @Scheduled(fixedDelay = "${core.rfid.scan-interval}")
    public void findRfidTags() {
        rfidService.scan();
    }

    private Map<RfidTagUid, String> mapToRfidTagUid(Map<String, String> rfidTagMapping) {
        return rfidTagMapping.entrySet().stream()
                .collect(Collectors.toMap(e -> new RfidTagUid(e.getKey()), Map.Entry::getValue));
    }

    private RfidService createRfidServiceInstance() {
        return halMockEnabled
                ? new RfidServiceMockImpl()
                : new RfidServiceImpl();
    }

    private GpioService createGpioServiceInstance() {
        return halMockEnabled
                ? new GpioServiceMockImpl()
                : new GpioServiceImpl();
    }

    private MpdService createMpdService() {
        return new MpdServiceImpl(mpdServerName, mpdServerPort);
    }

    private EventRouter createEventRouterInstance() {
        final Map<RfidTagUid, String> rfidTagUidStringMap = mapToRfidTagUid(rfidTagMapping);
        switch (EventRouterType.valueOf(eventRouterName)) {
            case InitPlaylistByRfidTagSelectTrackByGpi:
                return new InitPlaylistByRfidTagSelectTrackByGpi(rfidTagUidStringMap, rfidService, gpioService, mpdService);
            case SimplePlaylistLoad:
                return new SimplePlaylistLoad(rfidTagUidStringMap, rfidService, gpioService, mpdService);
            default:
                throw new IllegalArgumentException();
        }
    }
}
