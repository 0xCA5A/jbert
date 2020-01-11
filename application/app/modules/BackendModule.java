package modules;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Names;
import com.typesafe.config.Config;
import event.EventService;
import event.EventServiceImpl;
import event.router.EventRouterType;
import mpd.MpdService;
import mpd.MpdServiceImpl;

import javax.inject.Inject;

import static event.EventServiceImpl.EVENT_ROUTER_TYPE_NAME;


public class BackendModule extends AbstractModule {
    private final Config backendConfig;

    @Inject
    BackendModule(Config backendConfig) {
        this.backendConfig = backendConfig;
    }

    @Override
    protected void configure() {
        install(new HalModule(backendConfig.getBoolean("halMockEnabled")));

        final Config eventRouterConfig = backendConfig.getConfig("eventRouter");
        final String eventRouterName = eventRouterConfig.getString("name");
        final EventRouterType eventRouterType = EventRouterType.valueOf(eventRouterName);
        bindConstant().annotatedWith(Names.named(EVENT_ROUTER_TYPE_NAME)).to(eventRouterType);
        bind(EventService.class).to(EventServiceImpl.class).asEagerSingleton();

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
