package services;

import com.google.common.hash.Hashing;
import com.google.common.io.ByteSource;
import com.typesafe.config.Config;
import models.MetadataDto;
import models.TrackDto;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

import static java.nio.file.StandardOpenOption.CREATE;
import static utils.Strings.sanitizeFilename;

public class TrackService {

    private static final Logger log = LoggerFactory.getLogger(TrackService.class);

    private static final String FILE_SUFFIX = ".mp3";

    private static final String UNKNOWN_ARTIST = "Unknown Artist";
    private static final String UNKNOWN_ALBUM = "Unknown Album";
    private static final String UNKNOWN_TITLE = "Unknown Title";

    private final Config config;

    public TrackService(Config config) {
        this.config = config;
    }

    public TrackDto create(TrackDto track) throws IOException {
        if (track.getDataOptional().isPresent()) {
            final ByteSource data = ByteSource.wrap(Base64.getDecoder().decode(track.getDataOptional().get()));
            try (final OutputStream out = Files.newOutputStream(getFilePath(track), CREATE)) {
                out.write(data.read());
            }
        }
        return track;
    }

    private Path getFilePath(TrackDto track) throws IOException {
        return Paths.get(config.getString("location"), getRelativeFilePath(track));
    }

    public String getRelativeFilePath(TrackDto track) throws IOException {
        final ByteSource data = ByteSource.wrap(Base64.getDecoder().decode(track.getDataOptional().get()));
        final MetadataDto fromId3 = readId3Tags(data);
        return sanitizeFilename(track.getMetadataOptional().flatMap(MetadataDto::getArtistOptional)
                        .orElseGet(() -> fromId3.getArtistOptional().orElse(UNKNOWN_ARTIST))) + '/' +
                sanitizeFilename(track.getMetadataOptional().flatMap(MetadataDto::getAlbumOptional)
                        .orElseGet(() -> fromId3.getAlbumOptional().orElse(UNKNOWN_ALBUM))) + '/' +
                sanitizeFilename(track.getMetadataOptional().flatMap(MetadataDto::getTitleOptional)
                        .orElseGet(() -> fromId3.getTitleOptional().orElse(UNKNOWN_TITLE)) + FILE_SUFFIX);
    }

    private MetadataDto readId3Tags(ByteSource data) throws IOException {

        final Path tmpDir = Files.createDirectories(Paths.get(config.getString("location"), "_tmp"));
        try (final OutputStream out = Files.newOutputStream(
                Paths.get(tmpDir.toString(), data.hash(Hashing.sha256()).toString() + FILE_SUFFIX), CREATE)) {
            out.write(data.read());
        }

        try {
            final MetadataDto fromId3 = readId3Tags(tmpDir.toFile());
            Files.deleteIfExists(tmpDir);
            return fromId3;
        } catch (Exception e) {
            log.info("Could not read ID3 tags from file '{}': {}", tmpDir, e.getMessage());
        }
        return MetadataDto.newBuilder().build();
    }

    public MetadataDto readId3Tags(String file)
    throws ReadOnlyFileException, CannotReadException, TagException, InvalidAudioFrameException, IOException {
        return readId3Tags(Paths.get(config.getString("location"), file).toFile());
    }

    public MetadataDto readId3Tags(File file)
    throws IOException, TagException, ReadOnlyFileException, CannotReadException, InvalidAudioFrameException {
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
            log.info("Could not parse year '{}' as number: {}", year, e.getMessage());
        }

        return trackBuilder.build();
    }
}
