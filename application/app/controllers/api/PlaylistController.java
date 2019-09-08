package controllers.api;

import models.ErrorDto;
import models.MetadataDto;
import models.PlaylistDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.libs.Json;
import play.libs.typedmap.TypedKey;
import play.mvc.Action;
import play.mvc.Controller;
import play.mvc.Http.Request;
import play.mvc.Result;
import play.mvc.With;
import scala.Option;
import services.PlaylistService;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

public class PlaylistController extends Controller {

    private static final Logger log = LoggerFactory.getLogger(PlaylistController.class);

    private final PlaylistService playlistService;

    @Inject
    public PlaylistController(PlaylistService playlistService) {
        this.playlistService = playlistService;
    }

    /**
     * List all available Playlists
     */
    public Result list(Option<String> q) {
        final List<PlaylistDto> playlists = q.isDefined()
                ? playlistService.findAllByName(q.get())
                : playlistService.getAll();
        return ok(Json.toJson(playlists));
    }

    /**
     * Find a Playlist by name
     */
    public Result find(String name) {
        try {
            final Optional<PlaylistDto> playlist = playlistService.findOneByName(name);
            return playlist.isPresent()
                    ? ok(Json.toJson(playlist))
                    : notFound();
        } catch (Exception e) {
            // TODO: Move into ErrorHandler
            log.info("Find Playlist failed: '{}'", name, e);
            return internalServerError(Json.toJson(
                    new ErrorDto(INTERNAL_SERVER_ERROR, "Find Playlist failed: '" + name + '\'')));
        }
    }

    /**
     * Create a new Playlist
     */
    @With(BodyParser.class)
    public Result create(Request request) {

        final PlaylistDto playlist = request.attrs().get(Attrs.PLAYLIST);
        try {
            return created(Json.toJson(playlistService.create(playlist)));
        } catch (IllegalArgumentException e) {
            // TODO: Move into ErrorHandler
            return badRequest(Json.toJson(new ErrorDto(BAD_REQUEST, e.getMessage())));
        } catch (Exception e) {
            // TODO: Move into ErrorHandler
            log.info("Create Playlist failed: {}", playlist, e);
            return internalServerError(Json.toJson(
                    new ErrorDto(INTERNAL_SERVER_ERROR, "Create Playlist failed: " + playlist)));
        }
    }

    /**
     * Update an existing Playlist
     */
    @With(BodyParser.class)
    public Result update(String name, Request request) {

        final PlaylistDto update = request.attrs().get(Attrs.PLAYLIST);
        final Optional<PlaylistDto> original;
        try {
            original = playlistService.findOneByName(name);
            if (!original.isPresent()) {
                return notFound();
            }
        } catch (Exception e) {
            // TODO: Move into ErrorHandler
            log.info("Update Playlist failed: {}", update, e);
            return internalServerError(Json.toJson(
                    new ErrorDto(INTERNAL_SERVER_ERROR, "Update Playlist failed: " + update)));
        }

        try {
            return ok(Json.toJson(playlistService.update(original.get(), update.getNameOptional().isPresent()
                    ? update
                    : new PlaylistDto(original.get().getNameOptional().get(), update.getTracksOptional().get()))));
        } catch (IllegalArgumentException e) {
            // TODO: Move into ErrorHandler
            return badRequest(Json.toJson(new ErrorDto(BAD_REQUEST, e.getMessage())));
        } catch (Exception e) {
            // TODO: Move into ErrorHandler
            log.info("Update Playlist failed: {}", update, e);
            return internalServerError(Json.toJson(
                    new ErrorDto(INTERNAL_SERVER_ERROR, "Update Playlist failed: " + update)));
        }
    }

    /**
     * Delete a Playlist
     */
    public Result delete(String name) {

        final Optional<PlaylistDto> playlist;
        try {
            playlist = playlistService.findOneByName(name);
            if (!playlist.isPresent()) {
                return notFound();
            }
        } catch (Exception e) {
            // TODO: Move into ErrorHandler
            log.info("Delete Playlist failed: '{}'", name, e);
            return internalServerError(Json.toJson(
                    new ErrorDto(INTERNAL_SERVER_ERROR, "Delete Playlist failed: '" + name + '\'')));
        }

        try {
            return ok(Json.toJson(playlistService.delete(playlist.get())));
        } catch (Exception e) {
            // TODO: Move into ErrorHandler
            log.info("Delete Playlist failed: {}", playlist.get(), e);
            return internalServerError(Json.toJson(
                    new ErrorDto(INTERNAL_SERVER_ERROR, "Delete Playlist failed: " + playlist.get())));
        }
    }

    /**
     * List Tracks of a Playlist
     */
    public Result listTracks(String name, Option<String> q) {
        try {
            final Optional<PlaylistDto> playlist = playlistService.findOneByName(name);
            return playlist.isPresent()
                    ? ok(Json.toJson(playlist.flatMap(PlaylistDto::getTracksOptional)
                            .map(tracks -> q.isDefined()
                                    ? tracks.stream()
                                        .filter(track -> track.getMetadataOptional()
                                                .flatMap(MetadataDto::getTitleOptional)
                                                .map(title -> title.matches("(?i:.*" + q.get() + ".*)"))
                                                .orElse(false)
                                        ).collect(Collectors.toList())
                                    : tracks
                            ).orElse(Collections.emptyList())))
                    : notFound();
        } catch (Exception e) {
            // TODO: Move into ErrorHandler
            log.info("Find Playlist failed: '{}'", name, e);
            return internalServerError(Json.toJson(
                    new ErrorDto(INTERNAL_SERVER_ERROR, "Find Playlist failed: '" + name + '\'')));
        }
    }

    /**
     * Get a Track by Index
     */
    public Result getTrackByIndex(String name, int index) {
        try {
            final Optional<PlaylistDto> playlist = playlistService.findOneByName(name);
            final boolean hasIndex = playlist.flatMap(PlaylistDto::getTracksOptional)
                    .map(tracks -> tracks.size() > index)
                    .orElse(false);
            return playlist.isPresent() && hasIndex
                    ? ok(Json.toJson(playlist.flatMap(PlaylistDto::getTracksOptional).map(tracks -> tracks.get(index))))
                    : notFound();
        } catch (Exception e) {
            // TODO: Move into ErrorHandler
            log.info("Find Playlist failed: '{}'", name, e);
            return internalServerError(Json.toJson(
                    new ErrorDto(INTERNAL_SERVER_ERROR, "Find Playlist failed: '" + name + '\'')));
        }
    }
}

class Attrs {
    static final TypedKey<PlaylistDto> PLAYLIST = TypedKey.create("playlist");
}

class BodyParser extends Action.Simple {
    public CompletionStage<Result> call(Request request) {
        return delegate.call(request.addAttr(Attrs.PLAYLIST, Json.fromJson(request.body().asJson(), PlaylistDto.class)));
    }
}
