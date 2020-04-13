package ch.jbert.services;

import ch.jbert.models.MetadataDto;
import ch.jbert.models.Playlist;
import ch.jbert.models.PlaylistDto;
import ch.jbert.models.TrackDto;
import io.micronaut.context.annotation.Value;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

public class PlaylistService extends DataService<PlaylistDto> {

    private static final Logger LOG = LoggerFactory.getLogger(PlaylistService.class);
    private static final String FILE_SUFFIX = ".m3u";

    @Value("${restapi.playlists.path}")
    private String basePath;
    
    @Inject
    private TrackService trackService;

    @Override
    public PlaylistDto create(PlaylistDto playlist) throws IOException {

        if (exists(playlist)) {
            throw new IllegalArgumentException(String.format("Playlist '%s' already exists",
                    playlist.getNameOptional().orElse(null)));
        }
        return addTracks(playlist);
    }

    @Override
    public List<PlaylistDto> getAll() {

        LOG.debug("Reading playlists from folder {}", basePath);

        final String[] filenames = new File(basePath).list((dir, name) -> name.endsWith(FILE_SUFFIX));
        if (filenames == null) {
            throw new IllegalStateException(String.format("Given playlists path is not accessible: '%s'",
                    basePath));
        }
        return Arrays.stream(filenames)
                .map(filename -> filename.substring(0, filename.length() - FILE_SUFFIX.length()))
                .map(name -> new PlaylistDto(name, getTracks(name + FILE_SUFFIX)))
                .map(Playlist::wrap)
                .sorted()
                .map(Playlist::unwrap)
                .collect(Collectors.toList());
    }

    @Override
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

    private List<TrackDto> getTracks(String filename) {
        final Path file = Paths.get(basePath, filename);
        if (Files.notExists(file) || !Files.isReadable(file)) {
            throw new IllegalStateException(String.format("Cannot read tracks from playlist file '%s'", filename));
        }

        try (final BufferedReader reader = Files.newBufferedReader(file)) {
            return reader.lines()
                    .map(line -> {
                        try {
                            return trackService.readId3Tags(line);
                        } catch (Exception e) {
                            LOG.info("Could not read ID3 tags from file {}: {}", line, e.getMessage());
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
            throw new IllegalStateException(String.format("Cannot read tracks from playlist file '%s'", filename));
        }
    }

    @Override
    public PlaylistDto update(PlaylistDto original, PlaylistDto update) throws IOException {

        if (!original.getNameOptional().equals(update.getNameOptional()) && exists(update)) {
            throw new IllegalArgumentException(String.format("Playlist '%s' already exists",
                    update.getNameOptional().orElse(null)));
        }
        delete(original);
        return addTracks(update);
    }

    @Override
    public PlaylistDto delete(PlaylistDto playlist) throws IOException {
        final Optional<Path> filePath = getFilePath(playlist);
        if (filePath.isPresent()) {
            Files.deleteIfExists(filePath.get());
        }
        return playlist;
    }

    public PlaylistDto deleteTrackByIndex(PlaylistDto playlist, int index) throws IOException {

        final List<TrackDto> copiedTracks = playlist.getTracksOptional().map(ArrayList::new).orElseThrow(
                () -> new IllegalArgumentException(String.format("Could not get tracks from playlist '%s'", playlist)));

        // Delete track from copied tracks list
        copiedTracks.remove(index);

        final PlaylistDto update = playlist.getNameOptional().map(n -> new PlaylistDto(n, copiedTracks)).orElseThrow(
                () -> new IllegalArgumentException(String.format("Could not get name from playlist '%s'", playlist)));

        return update(playlist, update);
    }

    private boolean exists(PlaylistDto playlist) {
        return getFilePath(playlist).map(Files::exists).orElse(false);
    }

    private boolean notExists(PlaylistDto playlist) {
        return !exists(playlist);
    }

    private PlaylistDto addTracks(PlaylistDto playlist) throws IOException {

        final Path file = getFilePath(playlist).orElseThrow(
                () -> new IllegalArgumentException(String.format("Cannot get file path of playlist '%s'", playlist)));

        try (final BufferedWriter writer = Files.newBufferedWriter(file)) {
            for (TrackDto track : playlist.getTracksOptional().orElse(Collections.emptyList())) {
                writer.write(trackService.getRelativeFilePath(trackService.create(track)));
                writer.newLine();
            }
        }
        return playlist;
    }

    private Optional<Path> getFilePath(PlaylistDto playlist) {
        return playlist.getNameOptional().map(name -> Paths.get(basePath, name + FILE_SUFFIX));
    }
}
