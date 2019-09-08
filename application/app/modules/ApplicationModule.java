package modules;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Names;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.PlaylistService;
import services.TrackService;

import javax.inject.Named;


public class ApplicationModule extends AbstractModule {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationModule.class);
    private static final Config config = ConfigFactory.load();
    private static final String PLAYLISTS = "playlists";
    private static final String TRACKS = "tracks";

    @Override
    protected void configure() {
        printBuildInfo();

        final Config backendConfig = config.getConfig("backend");
        final Config apiConfig = config.getConfig("frontend.api");

        install(new BackendModule(backendConfig));

        bind(Config.class).annotatedWith(Names.named(PLAYLISTS)).toInstance(apiConfig.getConfig(PLAYLISTS));
        bind(Config.class).annotatedWith(Names.named(TRACKS)).toInstance(apiConfig.getConfig(TRACKS));
    }

    @Provides
    PlaylistService providePlaylistService(@Named(PLAYLISTS) Config playlistsConfig, TrackService trackService) {
        return new PlaylistService(playlistsConfig, trackService);
    }

    @Provides
    TrackService provideTrackService(@Named(TRACKS) Config tracksConfig) {
        return new TrackService(tracksConfig);
    }

    private void printBuildInfo() {
        String version = String.format("%s-%s @ %s",
                buildinfo.BuildInfo.name(),
                buildinfo.BuildInfo.version(),
                buildinfo.BuildInfo.gitHeadCommit()
                        .map(headCommit -> headCommit.substring(0, 8))
                        .getOrElse(() -> "none"));
        if (buildinfo.BuildInfo.gitUncommittedChanges()) {
            version += "~dirty";
        }
        logger.info("Build info: " + version);
    }
}
