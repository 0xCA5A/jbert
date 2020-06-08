package ch.jbert.services;

import com.google.common.hash.Hashing;
import com.google.common.io.ByteSource;
import ch.jbert.models.Metadata;
import ch.jbert.models.Track;
import ch.jbert.utils.ThrowingConsumer;
import ch.jbert.utils.ThrowingPredicate;
import io.micronaut.context.annotation.Value;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Singleton;

import static java.nio.file.StandardOpenOption.CREATE;
import static ch.jbert.utils.Strings.sanitizeFilename;

@Singleton
public class TrackService implements DataService<Track> {

    private static final Logger LOG = LoggerFactory.getLogger(TrackService.class);
    private static final String FILE_SUFFIX = ".mp3";

    private static final String UNKNOWN_ARTIST = "Unknown Artist";
    private static final String UNKNOWN_ALBUM = "Unknown Album";
    private static final String UNKNOWN_TITLE = "Unknown Title";

    @Value("${restapi.tracks.path}")
    private String basePath;

    @Override
    public Track create(Track track) {

        if (track.getData().isPresent()) {
            if (exists(track)) {
                LOG.info("Track '{}' already exists, attempting to overwrite it", track);
            }
            final ByteSource data = ByteSource.wrap(Base64.getDecoder().decode(track.getData().get()));
            final Optional<Path> file = getFilePath(track);
            file.ifPresent(ThrowingConsumer.of(f -> {
                Files.createDirectories(f.getParent());
                try (final OutputStream out = Files.newOutputStream(f)) {
                    out.write(data.read());
                }
                track.getMetadata().ifPresent(m -> writeId3Tags(f.toFile(), m));
            }));
        } else if (notExists(track)) {
            throw new IllegalArgumentException(
                    String.format("Create track failed due to missing track data: '%s'", track));
        }
        return track;
    }

    @Override
    public List<Track> getAll() throws IOException {

        LOG.debug("Reading playlists from folder {}", basePath);

        try (Stream<Path> files = Files.walk(Paths.get(basePath), FileVisitOption.FOLLOW_LINKS)
                .filter(p -> p.toString().endsWith(FILE_SUFFIX))) {
            return files.map(file -> {
                try {
                    return readId3Tags(file.toFile());
                } catch (Exception e) {
                    // FIXME: Create Metadata by file structure alternatively
                    LOG.warn("Could not read ID3 tags from track '{}', returning empty metadata", file);
                    return Metadata.newBuilder().build();
                }
            }).map(Track::new).sorted().collect(Collectors.toList());
        }
    }

    @Override
    public List<Track> findAllByName(String name) throws IOException {
        return (name == null || name.isEmpty())
                ? getAll()
                : filterByName(getAll(), name);
    }

    /**
     * Returns a sublist of tracks which's names match the given string.
     * 
     * @param tracks List of tracks to be filtered
     * @param name   Name query
     * @return Sublist of the given tracks
     */
    public List<Track> filterByName(List<Track> tracks, String name) {
        return tracks.stream()
                .filter(track -> track.getMetadata().flatMap(Metadata::getTitle)
                        .map(title -> title.matches("(?i:.*" + name + ".*)")).orElse(false))
                .collect(Collectors.toList());
    }

    public Optional<Track> findOneByHash(String hash) throws IOException {
        if (!isValidMD5(hash)) {
            throw new IllegalArgumentException(String.format("Provided hash is not a valid MD5 hash: '%s'", hash));
        }
        return getAll().stream()
                .filter(ThrowingPredicate.of(t -> Objects.equals(t.calculateMD5(), hash)))
                .findAny();
    }

    private boolean isValidMD5(String s) {
        return s.matches("^[a-fA-F0-9]{32}$");
    }

    @Override
    public Track update(Track original, Track update) {

        final Metadata metadata;
        if (original.getMetadata().isPresent() && original.getMetadata().isPresent()) {
            metadata = Metadata.merge(original.getMetadata().get(), update.getMetadata().get());
        } else {
            metadata = update.getMetadata()
                .orElseGet(() -> original.getMetadata()
                        .orElseThrow(() -> new IllegalStateException("Original track does not have metadata")));
        }
        final String data = update.getData()
                .orElseGet(() -> original.getData()
                        .orElseThrow(() -> new IllegalStateException("Original track does not have data")));
        delete(original);
        return create(new Track(metadata, data));
    }

    @Override
    public Track delete(Track track) {
        final Optional<Path> filePath = getFilePath(track);
        filePath.ifPresent(ThrowingConsumer.of(Files::deleteIfExists));
        return track;
    }

    private boolean exists(Track track) {
        return getFilePath(track).map(Files::exists).orElse(false);
    }

    private boolean notExists(Track track) {
        return !exists(track);
    }

    private Optional<Path> getFilePath(Track track) {
        try {
            return Optional.of(Paths.get(basePath, getRelativeFilePath(track)));
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    String getRelativeFilePath(Track track) throws IOException {
        final ByteSource byteSource = track.getData()
                .map(data -> ByteSource.wrap(Base64.getDecoder().decode(data)))
                .orElse(ByteSource.empty());
        final Metadata fromId3 = readId3Tags(byteSource);
        return sanitizeFilename(track.getMetadata().flatMap(Metadata::getArtist)
                        .orElseGet(() -> fromId3.getArtist().orElse(UNKNOWN_ARTIST))) + '/' +
                sanitizeFilename(track.getMetadata().flatMap(Metadata::getAlbum)
                        .orElseGet(() -> fromId3.getAlbum().orElse(UNKNOWN_ALBUM))) + '/' +
                sanitizeFilename(track.getMetadata().flatMap(Metadata::getTitle)
                        .orElseGet(() -> fromId3.getTitle().orElse(UNKNOWN_TITLE)) + FILE_SUFFIX);
    }

    private Metadata readId3Tags(ByteSource data) throws IOException {

        final Path tmpDir = Files.createDirectories(Paths.get(basePath, "_tmp"));
        final Path tmpFile = Paths.get(tmpDir.toString(), data.hash(Hashing.sha256()).toString() + FILE_SUFFIX);
        try (final OutputStream out = Files.newOutputStream(tmpFile, CREATE)) {
            out.write(data.read());
        }

        try {
            final Metadata fromId3 = readId3Tags(tmpFile.toFile());
            Files.deleteIfExists(tmpDir);
            return fromId3;
        } catch (Exception e) {
            LOG.info("Could not read ID3 tags from file '{}': {}", tmpFile, e.getMessage());
        }
        return Metadata.newBuilder().build();
    }

    Metadata readId3Tags(String file) throws Exception {
        return readId3Tags(Paths.get(basePath, file).toFile());
    }

    private Metadata readId3Tags(File file) throws Exception {
        final AudioFile audioFile = AudioFileIO.read(file);
        final AudioHeader audioHeader = audioFile.getAudioHeader();
        final Tag tag = audioFile.getTag();
        final Metadata.Builder trackBuilder = Metadata.newBuilder()
                .withDuration(audioHeader.getTrackLength())
                .withArtist(tag.getFirst(FieldKey.ARTIST))
                .withAlbum(tag.getFirst(FieldKey.ALBUM))
                .withTitle(tag.getFirst(FieldKey.TITLE))
                .withComment(tag.getFirst(FieldKey.COMMENT))
                .withGenre(tag.getFirst(FieldKey.GENRE));

        final String year = tag.getFirst(FieldKey.YEAR);
        try {
            trackBuilder.withYear(Integer.parseInt(year));
        } catch (Exception e) {
            LOG.info("Could not parse year '{}' as number: {}", year, e.getMessage());
        }

        return trackBuilder.build();
    }

    private void writeId3Tags(File file, Metadata metadata) {
        try {
            final AudioFile audioFile = AudioFileIO.read(file);
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
