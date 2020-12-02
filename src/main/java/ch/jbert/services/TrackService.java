package ch.jbert.services;

import com.google.common.base.Strings;
import com.google.common.io.ByteSource;
import ch.jbert.models.Metadata;
import ch.jbert.models.Track;
import ch.jbert.utils.ThrowingConsumer;
import ch.jbert.utils.ThrowingFunction;
import ch.jbert.utils.ThrowingPredicate;
import io.micronaut.context.annotation.Value;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Singleton;

import static ch.jbert.utils.Strings.sanitizeFilename;

@Singleton
public class TrackService implements DataService<Track> {

    private static final Logger LOG = LoggerFactory.getLogger(TrackService.class);
    private static final String TEMP_DIR = "_tmp";
    private static final String FILE_SUFFIX = ".mp3";
    private static final String SHA256_PATTERN = "^[A-Fa-f0-9]{64}$";

    private static final String UNKNOWN_ARTIST = "Unknown Artist";
    private static final String UNKNOWN_ALBUM = "Unknown Album";
    private static final String UNKNOWN_TITLE = "Unknown Title";

    @Value("${restapi.tracks.path}")
    private String basePath;

    @Override
    public Track create(Track track) throws IOException {

        Objects.requireNonNull(track);

        if (exists(track)) {
            throw new IllegalArgumentException(String.format("Track '%s' already exists, not going to overwrite it", track));
        }
        if (track.getData().isPresent()) {
            final ByteSource data = ByteSource.wrap(Base64.getDecoder().decode(track.getData().get()));
            final Path file = getFilePath(track);
            Files.createDirectories(file.getParent());
            try (final OutputStream out = Files.newOutputStream(file)) {
                out.write(data.read());
            }
            track.getMetadata().ifPresent(m -> writeId3Tags(file, m));
        } else {
            throw new IllegalArgumentException(
                    String.format("Create track failed due to missing track data: '%s'", track));
        }
        return Track.newBuilder()
                .withMetadata(readMetadata(track))
                .withData(Files.readAllBytes(getFilePath(track)))
                .build();
    }

    private Metadata readMetadata(Track track) {
        return track.getMetadata()
                .orElseGet(() -> readId3Tags(getFilePath(track)));
    }

    @Override
    public List<Track> getAll() throws IOException {

        LOG.debug("Reading tracks from folder {}", basePath);

        try (Stream<Path> files = Files.find(Paths.get(basePath), Integer.MAX_VALUE, trackFinder(), FileVisitOption.FOLLOW_LINKS)) {
            return files
                    .map(this::readId3Tags)
                    .map(Track::new)
                    .sorted().collect(Collectors.toList());
        }
    }

    private BiPredicate<Path, BasicFileAttributes> trackFinder() {
        return (path, attr) -> !attr.isDirectory() && !path.startsWith(Paths.get(basePath, TEMP_DIR)) &&
                path.toString().endsWith(FILE_SUFFIX);
    }

    @Override
    public List<Track> findAllByName(String name) throws IOException {
        return filterByName(getAll(), name);
    }

    /**
     * Returns a sublist of tracks which's names match the given string.
     * 
     * @param tracks List of tracks to be filtered
     * @param name   Name query
     * @return Sublist of the given tracks
     */
    public static List<Track> filterByName(List<Track> tracks, String name) {

        Objects.requireNonNull(tracks);

        if (Strings.isNullOrEmpty(name)) {
            return tracks;
        }

        return tracks.stream()
                .filter(track -> track.getMetadata()
                        .flatMap(Metadata::getTitle)
                        .map(title -> title.matches("(?i:.*" + name + ".*)"))
                        .orElse(false))
                .collect(Collectors.toList());
    }

    public Optional<Track> findOneByHash(String hash) throws IOException {
        LOG.debug("Looking up tracks by hash: {}", hash);
        if (!isValidSha256(hash)) {
            throw new IllegalArgumentException(String.format("Provided hash is not a valid MD5 hash: '%s'", hash));
        }
        return getAllWithData().stream()
                .filter(ThrowingPredicate.of(t -> Objects.equals(t.calculateSha256(), hash)))
                .findAny();
    }

    private boolean isValidSha256(String s) {
        return s.matches(SHA256_PATTERN);
    }

    private List<Track> getAllWithData() throws IOException {
        return getAll().stream()
                .map(ThrowingFunction.of(this::enrichTrackData))
                .collect(Collectors.toList());
    }

    private Track enrichTrackData(Track track) throws IOException {
        final Track enriched = track.getBuilder().withData(Files.readAllBytes(getFilePath(track))).build();
        LOG.trace("Enriched track {} with data: {}", enriched, enriched.getData());
        return enriched;
    }

    @Override
    public Track update(Track original, Track update) throws IOException {

        Objects.requireNonNull(original);
        Objects.requireNonNull(update);

        if (notExists(original)) {
            throw new IllegalArgumentException(String.format("Track '%s' does not exist", original));
        }

        // FIXME: Cannot unset single metadata
        final Metadata metadata;
        if (original.getMetadata().isPresent() && update.getMetadata().isPresent()) {
            metadata = Metadata.merge(original.getMetadata().get(), update.getMetadata().get());
        } else {
            metadata = update.getMetadata()
                .orElseGet(() -> original.getMetadata()
                        .orElse(null));
        }
        final String data = update.getData()
                .orElseGet(() -> original.getData()
                        .orElseThrow(() -> new IllegalStateException("Original track does not have data")));
        delete(original);
        return create(new Track(metadata, data));
    }

    @Override
    public Track delete(Track track) throws IOException {

        Objects.requireNonNull(track);

        if (notExists(track)) {
            throw new IllegalArgumentException(String.format("Track '%s' does not exist", track));
        }

        Files.delete(getFilePath(track));
        return track;
    }

    @Override
    public boolean exists(Track track) {
        return Files.exists(getFilePath(track));
    }

    private Path getFilePath(Track track) {
        return Paths.get(basePath, getRelativeFilePath(track));
    }

    String getRelativeFilePath(Track track) {
        final Metadata fromId3 = readId3Tags(track);
        return sanitizeFilename(track.getMetadata().flatMap(Metadata::getArtist)
                        .orElseGet(() -> fromId3.getArtist().orElse(UNKNOWN_ARTIST))) + '/' +
                sanitizeFilename(track.getMetadata().flatMap(Metadata::getAlbum)
                        .orElseGet(() -> fromId3.getAlbum().orElse(UNKNOWN_ALBUM))) + '/' +
                sanitizeFilename(track.getMetadata().flatMap(Metadata::getTitle)
                        .orElseGet(() -> fromId3.getTitle().orElse(UNKNOWN_TITLE)) + FILE_SUFFIX);
    }

    private Metadata readId3Tags(Track track) {
        return track.getData()
                .map(ThrowingFunction.of(d -> {
                    final ByteSource data = ByteSource.wrap(Base64.getDecoder().decode(d));
                    final Path tmpDir = Files.createDirectories(Paths.get(basePath, TEMP_DIR));
                    final Path tmpFile = tmpDir.resolve(track.calculateSha256() + FILE_SUFFIX);
                    try (final OutputStream out = Files.newOutputStream(tmpFile)) {
                        out.write(data.read());
                    }
                    return readId3Tags(tmpFile);
                }))
                .orElse(Metadata.newBuilder().build());
    }

    Metadata readId3Tags(String file) {
        return readId3Tags(Paths.get(basePath, file));
    }

    private Metadata readId3Tags(Path file) {

        LOG.debug("Attempting to read ID3 tags from file '{}'...", file);

        try {
            final AudioFile audioFile = AudioFileIO.read(file.toFile());
            final AudioHeader audioHeader = audioFile.getAudioHeader();
            final Tag tag = audioFile.getTag();
            final Metadata.Builder metadataBuilder = Metadata.newBuilder()
                    .withDuration(audioHeader.getTrackLength())
                    .withArtist(tag.getFirst(FieldKey.ARTIST))
                    .withAlbum(tag.getFirst(FieldKey.ALBUM))
                    .withTitle(tag.getFirst(FieldKey.TITLE))
                    .withComment(tag.getFirst(FieldKey.COMMENT))
                    .withGenre(tag.getFirst(FieldKey.GENRE));

            final String year = tag.getFirst(FieldKey.YEAR);
            try {
                metadataBuilder.withYear(Integer.parseInt(year));
            } catch (Exception e) {
                LOG.info("Could not parse year '{}' as number: {}", year, e.getMessage());
            }
            try {
                metadataBuilder.withYear(LocalDateTime.parse(year).getYear());
            } catch (Exception e) {
                LOG.info("Could not parse year '{}' as date: {}", year, e.getMessage());
            }

            return metadataBuilder.build();
        } catch (Exception e) {
            LOG.info("Could not read ID3 tags from file '{}', falling back to recovering metadata from file path: '{}'", file, e.getMessage());
            return Metadata.newBuilder()
                    .withArtist(file.getName(0).toString())
                    .withAlbum(file.getName(1).toString())
                    .withTitle(file.getName(2).toString())
                    .build();
        }
    }

    private void writeId3Tags(Path file, Metadata metadata) {
        try {
            final AudioFile audioFile = AudioFileIO.read(file.toFile());
            final Tag tag = audioFile.getTag();
            metadata.getArtist().ifPresent(ThrowingConsumer.of(v -> tag.setField(FieldKey.ARTIST, v)));
            metadata.getAlbum().ifPresent(ThrowingConsumer.of(v -> tag.setField(FieldKey.ALBUM, v)));
            metadata.getTitle().ifPresent(ThrowingConsumer.of(v -> tag.setField(FieldKey.TITLE, v)));
            metadata.getComment().ifPresent(ThrowingConsumer.of(v -> tag.setField(FieldKey.COMMENT, v)));
            metadata.getGenre().ifPresent(ThrowingConsumer.of(v -> tag.setField(FieldKey.GENRE, v)));
            audioFile.setTag(tag);
            audioFile.commit();
        } catch (Exception e) {
            LOG.warn("Could not write ID3 tags from track '{}'", file);
        }
    }
}
