package ch.jbert.services;

import ch.jbert.models.Playlist;
import ch.jbert.models.Track;
import ch.jbert.utils.ThrowingConsumer;
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
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class PlaylistService implements DataService<Playlist> {

    private static final Logger LOG = LoggerFactory.getLogger(PlaylistService.class);
    private static final String FILE_SUFFIX = ".m3u";

    @Value("${restapi.playlists.path}")
    private String basePath;
    
    @Inject
    private TrackService trackService;

    @Override
    public Playlist create(Playlist playlist) throws IOException {

        Objects.requireNonNull(playlist);

        if (exists(playlist)) {
            throw new IllegalArgumentException(String.format("Playlist '%s' already exists",
                    playlist.getName().orElse(null)));
        }
        return addTracks(playlist);
    }

    @Override
    public List<Playlist> getAll() {

        LOG.debug("Reading playlists from folder {}", basePath);

        final String[] filenames = new File(basePath).list((dir, name) -> name.endsWith(FILE_SUFFIX));
        if (filenames == null) {
            throw new IllegalStateException(String.format("Given playlists path is not accessible: '%s'",
                    basePath));
        }
        return Arrays.stream(filenames)
                .map(filename -> filename.substring(0, filename.length() - FILE_SUFFIX.length()))
                .map(name -> new Playlist(name, getTracks(name + FILE_SUFFIX)))
                .sorted()
                .collect(Collectors.toList());
    }

    @Override
    public List<Playlist> findAllByName(String name) {

        if (name == null || name.isEmpty()) {
            return getAll();
        }

        return getAll().stream()
                .filter(playlist -> playlist.getName().map(n -> n.matches("(?i:.*" + name + ".*)"))
                        .orElse(false))
                .collect(Collectors.toList());
    }

    public Optional<Playlist> findOneByName(String name) {
        return getAll().stream()
                .filter(playlist -> playlist.getName().map(n -> n.equals(name)).orElse(false))
                .findFirst();
    }

    private List<Track> getTracks(String filename) {
        final Path file = Paths.get(basePath, filename);
        if (Files.notExists(file) || !Files.isReadable(file)) {
            throw new IllegalStateException(String.format("Cannot read tracks from playlist file '%s'", filename));
        }

        try (final BufferedReader reader = Files.newBufferedReader(file)) {
            return reader.lines()
                    .map(trackService::readId3Tags)
                    .map(metadata -> Track.newBuilder().withMetadata(metadata).build())
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new IllegalStateException(String.format("Cannot read tracks from playlist file '%s'", filename));
        }
    }

    @Override
    public Playlist update(Playlist original, Playlist update) throws IOException {

        Objects.requireNonNull(original);
        Objects.requireNonNull(update);

        if (!update.getName().isPresent()) {
            String originalName = original.getName()
                    .orElseThrow(() -> new IllegalStateException("Original playlist does not have a name"));
            update = new Playlist(originalName, update.getTracks());
        }
        if (!original.getName().equals(update.getName()) && exists(update)) {
            throw new IllegalArgumentException(String.format("Playlist '%s' already exists",
                    update.getName().orElse(null)));
        } else if (notExists(original)) {
            throw new IllegalArgumentException(String.format("Playlist '%s' does not exist", update));
        }
        delete(original);
        return addTracks(update);
    }

    @Override
    public Playlist delete(Playlist playlist) throws IOException {

        Objects.requireNonNull(playlist);

        if (notExists(playlist)) {
            throw new IllegalArgumentException(String.format("Playlist '%s' does not exist", playlist));
        }

        getFilePath(playlist).ifPresent(ThrowingConsumer.of(Files::delete));
        return playlist;
    }

    public Playlist addTrack(Playlist playlist, Track track) throws IOException {

        Objects.requireNonNull(playlist);
        Objects.requireNonNull(track);

        if (notExists(playlist)) {
            throw new IllegalArgumentException(String.format("Playlist '%s' does not exist", playlist));
        }

        final List<Track> tracks = playlist.getTracks();
        tracks.add(track);
        return addTracks(playlist.getBuilder().withTracks(tracks).build());
    }

    public Playlist deleteTrackByIndex(Playlist playlist, int index) throws IOException {

        Objects.requireNonNull(playlist);

        final String playlistName = playlist.getName().orElseThrow(
            () -> new IllegalArgumentException(String.format("Could not get name from playlist '%s'", playlist)));

        final List<Track> tracks = findOneByName(playlistName).map(Playlist::getTracks).orElseThrow(
            () -> new IllegalArgumentException(String.format("Playlist '%s' does not exist", playlist)));
        tracks.remove(index);

        return update(playlist, new Playlist(playlistName, tracks));
    }

    @Override
    public boolean exists(Playlist playlist) {
        return getFilePath(playlist).map(Files::exists).orElse(false);
    }

    private Playlist addTracks(Playlist playlist) throws IOException {

        final Path file = getFilePath(playlist).orElseThrow(
                () -> new IllegalArgumentException(String.format("Cannot get file path of playlist '%s'", playlist)));

        try (final BufferedWriter writer = Files.newBufferedWriter(file)) {
            for (Track track : playlist.getTracks()) {
                if (trackService.notExists(track)) {
                    trackService.create(track);
                }
                writer.write(trackService.getRelativeFilePath(track));
                writer.newLine();
            }
        }
        return playlist;
    }

    private Optional<Path> getFilePath(Playlist playlist) {
        return playlist.getName().map(name -> Paths.get(basePath, name + FILE_SUFFIX));
    }
}
