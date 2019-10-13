package modules;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.typesafe.config.Config;
import event.EventRouter;
import event.EventRouterImpl;
import mpd.MpdService;
import mpd.MpdServiceImpl;

import javax.inject.Inject;


public class BackendModule extends AbstractModule {

    private final Config backendConfig;

    @Inject
    BackendModule(Config backendConfig) {
        this.backendConfig = backendConfig;
    }

    @Override
    protected void configure() {
        bind(EventRouter.class).to(EventRouterImpl.class).asEagerSingleton();

        install(new HalModule(backendConfig.getBoolean("halMockEnabled")));
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
