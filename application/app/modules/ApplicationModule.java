package modules;

import com.google.inject.AbstractModule;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import util.LogHelper;

import java.util.logging.Logger;


public class ApplicationModule extends AbstractModule {
    private static final Logger logger = LogHelper.getLogger(ApplicationModule.class.getName());

    private final Config config = ConfigFactory.load();

    @Override
    protected void configure() {
        printBuildInfo();

        install(new BackendModule(config.getConfig("backend")));
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
