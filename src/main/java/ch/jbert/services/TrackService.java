package ch.jbert.services;

import com.google.common.hash.Hashing;
import com.google.common.io.ByteSource;
import ch.jbert.models.MetadataDto;
import ch.jbert.models.Track;
import ch.jbert.models.TrackDto;
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
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.nio.file.StandardOpenOption.CREATE;
import static ch.jbert.utils.Strings.sanitizeFilename;

public class TrackService extends DataService<TrackDto> {

    private static final Logger LOG = LoggerFactory.getLogger(TrackService.class);
    private static final String FILE_SUFFIX = ".mp3";

    private static final String UNKNOWN_ARTIST = "Unknown Artist";
    private static final String UNKNOWN_ALBUM = "Unknown Album";
    private static final String UNKNOWN_TITLE = "Unknown Title";

    @Value("${restapi.tracks.path}")
    private String basePath;

    @Override
    public TrackDto create(TrackDto track) throws IOException {

        if (track.getDataOptional().isPresent()) {
            if (exists(track)) {
                LOG.info("Track '{}' already exists, attempting to overwrite it", track);
            }
            final ByteSource data = ByteSource.wrap(Base64.getDecoder().decode(track.getDataOptional().get()));
            final Optional<Path> file = getFilePath(track);
            if (file.isPresent()) {
                Files.createDirectories(file.get().getParent());
                try (final OutputStream out = Files.newOutputStream(file.get())) {
                    // FIXME: Write ID3 tags based on provided TrackDto.MetadataDto
                    out.write(data.read());
                }
            }
        } else if (notExists(track)) {
            throw new IllegalArgumentException(String.format("Create track failed due to missing track data: '%s'",
                    track));
        }
        return track;
    }

    @Override
    public List<TrackDto> getAll() throws IOException {

        LOG.debug("Reading playlists from folder {}", basePath);

        try (Stream<Path> files = Files.walk(Paths.get(basePath), FileVisitOption.FOLLOW_LINKS)
                .filter(p -> p.toString().endsWith(FILE_SUFFIX))) {
            return files
                    .map(file -> {
                        try {
                            // FIXME: Create MetadataDto by file structure alternatively
                            return readId3Tags(file.toFile());
                        } catch (Exception e) {
                            LOG.warn("Could not read ID3 tags from track '{}', returning empty metadata", file);
                            return MetadataDto.newBuilder().build();
                        }
                    })
                    .map(metadata -> new TrackDto(metadata, null))
                    .map(Track::wrap)
                    .sorted()
                    .map(Track::unwrap)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public List<TrackDto> findAllByName(String name) throws IOException {

        if (name == null || name.isEmpty()) {
            return getAll();
        }

        return getAll().stream()
                .filter(track -> {
                    final String pattern = "(?i:.*" + name + ".*)";
                    final Optional<MetadataDto> metadataOptional = track.getMetadataOptional();
                    final String artist = metadataOptional.flatMap(MetadataDto::getArtistOptional).orElse("");
                    final String album = metadataOptional.flatMap(MetadataDto::getAlbumOptional).orElse("");
                    final String title = metadataOptional.flatMap(MetadataDto::getTitleOptional).orElse("");
                    return Stream.of(artist, album, title).anyMatch(s -> s.matches(pattern));
                })
                .collect(Collectors.toList());
    }

    public Optional<TrackDto> findOneByHash(String hash) {
        return Optional.empty();
    }

    @Override
    public TrackDto update(TrackDto originalDto, TrackDto updateDto) {
        return null;
    }

    @Override
    public TrackDto delete(TrackDto dto) {
        return null;
    }

    private boolean exists(TrackDto track) {
        return getFilePath(track).map(Files::exists).orElse(false);
    }

    private boolean notExists(TrackDto track) {
        return !exists(track);
    }

    private Optional<Path> getFilePath(TrackDto track) {
        try {
            return Optional.of(Paths.get(basePath, getRelativeFilePath(track)));
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    String getRelativeFilePath(TrackDto track) throws IOException {
        final ByteSource byteSource = track.getDataOptional()
                .map(data -> ByteSource.wrap(Base64.getDecoder().decode(data)))
                .orElse(ByteSource.empty());
        final MetadataDto fromId3 = readId3Tags(byteSource);
        return sanitizeFilename(track.getMetadataOptional().flatMap(MetadataDto::getArtistOptional)
                        .orElseGet(() -> fromId3.getArtistOptional().orElse(UNKNOWN_ARTIST))) + '/' +
                sanitizeFilename(track.getMetadataOptional().flatMap(MetadataDto::getAlbumOptional)
                        .orElseGet(() -> fromId3.getAlbumOptional().orElse(UNKNOWN_ALBUM))) + '/' +
                sanitizeFilename(track.getMetadataOptional().flatMap(MetadataDto::getTitleOptional)
                        .orElseGet(() -> fromId3.getTitleOptional().orElse(UNKNOWN_TITLE)) + FILE_SUFFIX);
    }

    private MetadataDto readId3Tags(ByteSource data) throws IOException {

        final Path tmpDir = Files.createDirectories(Paths.get(basePath, "_tmp"));
        final Path tmpFile = Paths.get(tmpDir.toString(), data.hash(Hashing.sha256()).toString() + FILE_SUFFIX);
        try (final OutputStream out = Files.newOutputStream(tmpFile, CREATE)) {
            out.write(data.read());
        }

        try {
            final MetadataDto fromId3 = readId3Tags(tmpFile.toFile());
            Files.deleteIfExists(tmpDir);
            return fromId3;
        } catch (Exception e) {
            LOG.info("Could not read ID3 tags from file '{}': {}", tmpFile, e.getMessage());
        }
        return MetadataDto.newBuilder().build();
    }

    MetadataDto readId3Tags(String file) throws Exception {
        return readId3Tags(Paths.get(basePath, file).toFile());
    }

    private MetadataDto readId3Tags(File file) throws Exception {
        final AudioFile audioFile = AudioFileIO.read(file);
        final AudioHeader audioHeader = audioFile.getAudioHeader();
        final Tag tag = audioFile.getTag();
        final MetadataDto.Builder trackBuilder = MetadataDto.newBuilder()
                .setDuration(audioHeader.getTrackLength())
                .setArtist(tag.getFirst(FieldKey.ARTIST))
                .setAlbum(tag.getFirst(FieldKey.ALBUM))
                .setTitle(tag.getFirst(FieldKey.TITLE))
                .setComment(tag.getFirst(FieldKey.COMMENT))
                .setGenre(tag.getFirst(FieldKey.GENRE));

        final String year = tag.getFirst(FieldKey.YEAR);
        try {
            trackBuilder.setYear(Integer.parseInt(year));
        } catch (Exception e) {
            LOG.info("Could not parse year '{}' as number: {}", year, e.getMessage());
        }

        return trackBuilder.build();
    }
}
