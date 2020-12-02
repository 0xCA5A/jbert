package ch.jbert.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.stream.Stream;

public class FileUtils {

    public static void deleteRecursively(Path path) throws IOException {

        if (!Files.exists(path)) {
            return;
        }

        try (Stream<Path> files = Files.walk(path)) {
            files.sorted(Comparator.reverseOrder())
                .forEach(ThrowingConsumer.of(Files::delete));
        }
    }
}
