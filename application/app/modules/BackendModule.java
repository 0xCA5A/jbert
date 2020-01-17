package modules;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.typesafe.config.Config;
import event.EventService;
import event.EventServiceConfig;
import event.EventServiceImpl;
import event.router.EventRouterType;
import mpd.MpdService;
import mpd.MpdServiceImpl;
import rfid.RfidTagUid;

import java.util.Map;
import java.util.stream.Collectors;


public class BackendModule extends AbstractModule {
    private final Config backendConfig;

    BackendModule(Config backendConfig) {
        this.backendConfig = backendConfig;
    }

    @Override
    protected void configure() {
        install(new HalModule(backendConfig.getBoolean("halMockEnabled")));
        bind(EventService.class).to(EventServiceImpl.class).asEagerSingleton();
    }

    @Provides
    private EventServiceConfig provideEventServiceConfig() {
        // Parse event router type
        final Config eventRouterConfig = backendConfig.getConfig("eventRouter");
        final String eventRouterName = eventRouterConfig.getString("name");
        final EventRouterType eventRouterType = EventRouterType.valueOf(eventRouterName);

        // Parse RFID tag mapping
        final Config rfidTagConfig = backendConfig.getConfig("rfidTag");
        final Map<RfidTagUid, String> rfidTagMapping = parseRfidTagMappingConfig(rfidTagConfig.getConfig("mapping"));

        return new EventServiceConfig(eventRouterType, rfidTagMapping);
    }

    private Map<RfidTagUid, String> parseRfidTagMappingConfig(Config rfidTagMappingConfig) {
        return rfidTagMappingConfig.entrySet().stream()
                .collect(Collectors.toMap(e -> new RfidTagUid(e.getKey()),
                        e -> e.getValue().unwrapped().toString().replace("\"", "")));
    }

    @Provides
    public MpdService provideMpdService() {
        final Config mpdConfig = backendConfig.getConfig("mpd");
        MpdServiceImpl mpdService = new MpdServiceImpl(mpdConfig.getString("server"), mpdConfig.getInt("port"));

        mpdService.ensureConnection();
        mpdService.configure();

        return mpdService;
    }
}
