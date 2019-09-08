package services;

import com.typesafe.config.Config;
import models.MetadataDto;
import models.Playlist;
import models.PlaylistDto;
import models.TrackDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class PlaylistService {

    private static final Logger log = LoggerFactory.getLogger(PlaylistService.class);

    private static final String FILE_SUFFIX = ".m3u";

    private final Config config;
    private final TrackService trackService;

    @Inject
    public PlaylistService(Config config, TrackService trackService) {
        this.config = config;
        this.trackService = trackService;
    }

    public List<PlaylistDto> findAllByName(String name) {

        if (name == null || name.isEmpty()) {
            return getAll();
        }

        return getAll().stream()
                .filter(playlist -> playlist.getNameOptional().map(n -> n.matches("(?i:.*" + name + ".*)"))
                        .orElse(false))
                .collect(Collectors.toList());
    }

    public Optional<PlaylistDto> findOneByName(String name) {
        return getAll().stream()
                .filter(playlist -> playlist.getNameOptional().map(n -> n.equals(name)).orElse(false))
                .findFirst();
    }

    public List<PlaylistDto> getAll() {

        final String playlistsPath = config.getString("location");
        log.debug("Reading playlists from folder {}", playlistsPath);

        final String[] filenames = new File(playlistsPath).list((dir, name) -> name.endsWith(FILE_SUFFIX));
        if (filenames == null) {
            throw new IllegalStateException("Given playlists path is not accessible: '" + playlistsPath + '\'');
        }
        return Arrays.stream(filenames)
                .map(filename -> filename.endsWith(FILE_SUFFIX)
                        ? filename.substring(0, filename.length() - FILE_SUFFIX.length())
                        : filename)
                .map(name -> new PlaylistDto(name, getTracks(name + FILE_SUFFIX)))
                .map(Playlist::wrap)
                .sorted()
                .map(Playlist::unwrap)
                .collect(Collectors.toList());
    }

    private List<TrackDto> getTracks(String filename) {
        final Path file = Paths.get(config.getString("location"), filename);
        if (Files.notExists(file) || !Files.isReadable(file)) {
            throw new IllegalStateException("Cannot read tracks from playlist file '" + filename + '\'');
        }

        try (final BufferedReader reader = Files.newBufferedReader(file)) {
            return reader.lines()
                    .map(line -> {
                        try {
                            return trackService.readId3Tags(line);
                        } catch (Exception e) {
                            log.info("Could not read ID3 tags from file {}: {}", line, e.getMessage());
                            final List<String> entry = Arrays.asList(line.split("/"));
                            return MetadataDto.newBuilder()
                                    .setArtist(entry.get(0))
                                    .setAlbum(entry.get(1))
                                    .setTitle(entry.get(2))
                                    .build();
                        }
                    })
                    .map(metadata -> TrackDto.newBuilder().setMetadata(metadata).build())
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read tracks from playlist file '" + filename + '\'');
        }
    }

    public PlaylistDto create(PlaylistDto playlist) throws IOException {

        if (exists(playlist)) {
            throw new IllegalArgumentException("Playlist '" + playlist.getNameOptional().orElse(null) + "' already exists");
        }
        return addTracks(playlist);
    }

    public PlaylistDto update(PlaylistDto original, PlaylistDto update) throws IOException {

        if (!original.getNameOptional().equals(update.getNameOptional()) && exists(update)) {
            throw new IllegalArgumentException("Playlist '" + update.getNameOptional().orElse(null) + "' already exists");
        }
        delete(original);
        return addTracks(update);
    }

    private PlaylistDto addTracks(PlaylistDto playlist) throws IOException {

        final Path file = getFilePath(playlist);

        try (final BufferedWriter writer = Files.newBufferedWriter(file)) {
            for (TrackDto track : playlist.getTracksOptional().orElse(Collections.emptyList())) {
                if (track.getDataOptional().isPresent()) {
                    trackService.create(track);
                }
                writer.write(trackService.getRelativeFilePath(track));
                writer.newLine();
            }
        }
        return playlist;
    }

    public PlaylistDto delete(PlaylistDto playlist) throws IOException {
        Files.deleteIfExists(getFilePath(playlist));
        return playlist;
    }

    public boolean exists(PlaylistDto playlist) {
        return Files.exists(getFilePath(playlist));
    }

    public boolean notExists(PlaylistDto playlist) {
        return !exists(playlist);
    }

    private Path getFilePath(PlaylistDto playlist) {
        return Paths.get(config.getString("location"), playlist.getNameOptional().get() + FILE_SUFFIX);
    }
}
