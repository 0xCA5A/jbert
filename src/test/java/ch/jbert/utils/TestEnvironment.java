package ch.jbert.utils;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.micronaut.context.annotation.Value;

public class TestEnvironment {

    private static final Logger LOG = LoggerFactory.getLogger(TestEnvironment.class);

    @Value("${restapi.playlists.path}")
    private String playlistsBasePath;

    @Value("${restapi.tracks.path}")
    private String tracksBasePath;

    @BeforeEach
    void clearFiles() {
        Arrays.asList(playlistsBasePath, tracksBasePath).stream()
                .peek(p -> LOG.info("Clearing directory {} ...", p))
                .map(Paths::get)
                .peek(ThrowingConsumer.of(FileUtils::deleteRecursively))
                .forEach(ThrowingConsumer.of(Files::createDirectories));
    }

}
