package ch.jbert.controllers.api;

import ch.jbert.models.ErrorDto;
import ch.jbert.models.MetadataDto;
import ch.jbert.models.PlaylistDto;
import ch.jbert.services.PlaylistService;
import ch.jbert.utils.ThrowingFunction;
import ch.jbert.utils.ThrowingSupplier;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Put;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oracle.svm.core.annotate.Delete;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import static io.micronaut.http.HttpResponse.badRequest;
import static io.micronaut.http.HttpResponse.created;
import static io.micronaut.http.HttpResponse.notFound;
import static io.micronaut.http.HttpResponse.ok;
import static io.micronaut.http.HttpResponse.serverError;

@Controller("/api/playlists")
public class PlaylistController {

    private static final Logger LOG = LoggerFactory.getLogger(PlaylistController.class);

    @Inject
    private PlaylistService playlistService;

    /**
     * List all available Playlists
     */
    @Get("{?q}")
    public HttpResponse list(Optional<String> q) {
        final List<PlaylistDto> playlists = q
                .map(ThrowingFunction.of(n -> playlistService.findAllByName(n)))
                .orElseGet(ThrowingSupplier.of(() -> playlistService.getAll()));
        return ok(playlists);
    }

    /**
     * Find a Playlist by name
     */
    @Get("/{name}")
    public HttpResponse find(String name) {
        try {
            return playlistService.findOneByName(name)
                    .map(HttpResponse::ok)
                    .orElse(notFound());
        } catch (Exception e) {
            // TODO: Move into ErrorHandler
            LOG.info("Find Playlist failed: '{}'", name, e);
            return serverError(
                    new ErrorDto(HttpStatus.INTERNAL_SERVER_ERROR.getCode(), String.format("Find Playlist failed: '%s'", 
                            name)));
        }
    }

    /**
     * Create a new Playlist
     */
    @Post
    public HttpResponse create(@Body PlaylistDto playlist) {

        try {
            return created(playlistService.create(playlist));
        } catch (IllegalArgumentException e) {
            // TODO: Move into ErrorHandler
            return badRequest(new ErrorDto(HttpStatus.BAD_REQUEST.getCode(), e.getMessage()));
        } catch (Exception e) {
            // TODO: Move into ErrorHandler
            LOG.info("Create Playlist failed: {}", playlist, e);
            return serverError(
                    new ErrorDto(HttpStatus.INTERNAL_SERVER_ERROR.getCode(), String.format("Create Playlist failed: '%s'", 
                            playlist)));
        }
    }

    /**
     * Update an existing Playlist
     */
    @Put("/{name}")
    public HttpResponse update(String name, @Body PlaylistDto playlist) {

        final Optional<PlaylistDto> original;
        try {
            original = playlistService.findOneByName(name);
            if (!original.isPresent()) {
                return notFound();
            }
        } catch (Exception e) {
            // TODO: Move into ErrorHandler
            LOG.info("Update Playlist failed: {}", playlist, e);
            return serverError(
                    new ErrorDto(HttpStatus.INTERNAL_SERVER_ERROR.getCode(), String.format("Update Playlist failed: '%s'", 
                            playlist)));
        }

        try {
            return ok(playlistService.update(original.get(), playlist.getNameOptional().isPresent()
                    ? playlist
                    : new PlaylistDto(original.get().getNameOptional().get(), playlist.getTracksOptional().get())));
        } catch (IllegalArgumentException e) {
            // TODO: Move into ErrorHandler
            return badRequest(new ErrorDto(HttpStatus.BAD_REQUEST.getCode(), e.getMessage()));
        } catch (Exception e) {
            // TODO: Move into ErrorHandler
            LOG.info("Update Playlist failed: {}", playlist, e);
            return serverError(
                    new ErrorDto(HttpStatus.INTERNAL_SERVER_ERROR.getCode(), String.format("Update Playlist failed: '%s'", 
                            playlist)));
        }
    }

    /**
     * Delete a Playlist
     */
    @Delete("/{name}")
    public HttpResponse delete(String name) {

        final Optional<PlaylistDto> playlist;
        try {
            playlist = playlistService.findOneByName(name);
            if (!playlist.isPresent()) {
                return notFound();
            }
        } catch (Exception e) {
            // TODO: Move into ErrorHandler
            LOG.info("Delete Playlist failed: '{}'", name, e);
            return serverError(
                    new ErrorDto(HttpStatus.INTERNAL_SERVER_ERROR.getCode(), String.format("Delete Playlist failed: '%s'",
                            name)));
        }

        try {
            return ok(playlistService.delete(playlist.get()));
        } catch (Exception e) {
            // TODO: Move into ErrorHandler
            LOG.info("Delete Playlist failed: {}", playlist.get(), e);
            return serverError(
                    new ErrorDto(HttpStatus.INTERNAL_SERVER_ERROR.getCode(), String.format("Delete Playlist failed: '%s'",
                            playlist.get())));
        }
    }

    /**
     * List Tracks of a Playlist
     */
    @Get("/{name}/tracks{?q}")
    public HttpResponse listTracks(String name, Optional<String> q) {
        try {
            final Optional<PlaylistDto> playlist = playlistService.findOneByName(name);
            return playlist.isPresent()
                    ? ok(playlist.flatMap(PlaylistDto::getTracksOptional)
                            .map(tracks -> q.isPresent()
                                    ? tracks.stream()
                                            .filter(track -> track.getMetadataOptional()
                                                    .flatMap(MetadataDto::getTitleOptional)
                                                    .map(title -> title.matches("(?i:.*" + q.get() + ".*)"))
                                                    .orElse(false)
                                            ).collect(Collectors.toList())
                                    : tracks
                            ).orElse(Collections.emptyList()))
                    : notFound();
        } catch (IllegalArgumentException e) {
            // TODO: Move into ErrorHandler
            return badRequest(new ErrorDto(HttpStatus.BAD_REQUEST.getCode(), e.getMessage()));
        } catch (Exception e) {
            // TODO: Move into ErrorHandler
            LOG.info("Find Playlist failed: '{}'", name, e);
            return serverError(
                    new ErrorDto(HttpStatus.INTERNAL_SERVER_ERROR.getCode(), String.format("Find Playlist failed: '%s'",
                            name)));
        }
    }

    /**
     * Get a Track by Index
     */
    @Get("/{name}/tracks/{index}")
    public HttpResponse getTrackByIndex(String name, int index) {
        try {
            final Optional<PlaylistDto> playlist = playlistService.findOneByName(name);
            final boolean hasIndex = playlist.flatMap(PlaylistDto::getTracksOptional)
                    .map(tracks -> tracks.size() > index)
                    .orElse(false);
            return playlist.isPresent() && hasIndex
                    ? ok(playlist.flatMap(PlaylistDto::getTracksOptional).map(tracks -> tracks.get(index)))
                    : notFound();
        } catch (IllegalArgumentException e) {
            // TODO: Move into ErrorHandler
            return badRequest(new ErrorDto(HttpStatus.BAD_REQUEST.getCode(), e.getMessage()));
        } catch (Exception e) {
            // TODO: Move into ErrorHandler
            LOG.info("Find Playlist failed: '{}'", name, e);
            return serverError(
                    new ErrorDto(HttpStatus.INTERNAL_SERVER_ERROR.getCode(), String.format("Get Track #%d from Playlist failed: '%s'",
                            index, name)));
        }
    }

    /**
     * Delete a Track by Index
     */
    @Delete("/{name}/tracks/{index}")
    public HttpResponse deleteTrackByIndex(String name, int index) {
        try {
            final Optional<PlaylistDto> playlist = playlistService.findOneByName(name);
            final boolean hasIndex = playlist.flatMap(PlaylistDto::getTracksOptional)
                    .map(tracks -> tracks.size() > index)
                    .orElse(false);
            return playlist.isPresent() && hasIndex
                    ? ok(playlistService.deleteTrackByIndex(playlist.get(), index))
                    : notFound();
        } catch (IllegalArgumentException e) {
            // TODO: Move into ErrorHandler
            return badRequest(new ErrorDto(HttpStatus.BAD_REQUEST.getCode(), e.getMessage()));
        } catch (Exception e) {
            // TODO: Move into ErrorHandler
            LOG.info("Find Playlist failed: '{}'", name, e);
            return serverError(
                    new ErrorDto(HttpStatus.INTERNAL_SERVER_ERROR.getCode(), String.format("Delete Track #%d Playlist failed: '%s'",
                            index, name)));
        }
    }
}
