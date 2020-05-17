package ch.jbert.controllers.api;

import ch.jbert.models.Error;
import ch.jbert.models.Playlist;
import ch.jbert.models.Track;
import ch.jbert.services.PlaylistService;
import ch.jbert.utils.ThrowingFunction;
import ch.jbert.utils.ThrowingSupplier;
import ch.jbert.utils.UncheckedException;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Put;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import static io.micronaut.http.HttpResponse.badRequest;
import static io.micronaut.http.HttpResponse.created;
import static io.micronaut.http.HttpResponse.notFound;
import static io.micronaut.http.HttpResponse.ok;
import static io.micronaut.http.HttpResponse.serverError;

@Controller("/api/playlists")
@Tag(name = "${api.tags.playlists.name}")
public class PlaylistController {

    private static final Logger LOG = LoggerFactory.getLogger(PlaylistController.class);

    @Inject
    private PlaylistService playlistService;

    /**
     * List all available Playlists
     */
    @Get("{?q}")
    @ApiResponse(
        responseCode = "200",
        content = @Content(
            array = @ArraySchema(
                schema = @Schema(implementation = Playlist.class)
            )
        )
    )
    @ApiResponse(
        responseCode = "500",
        description = "Server Error",
        content = @Content(
            schema = @Schema(implementation = Error.class)
        )
    )
    public HttpResponse list(
        @Parameter(description = "Filter playlists by name", required = false) Optional<String> q) {
        try {
            final List<Playlist> playlists = q
                .map(ThrowingFunction.of(n -> playlistService.findAllByName(n)))
                .orElseGet(ThrowingSupplier.of(() -> playlistService.getAll()));
            return ok(playlists);
        } catch (Exception e) {
            // TODO: Move into ErrorHandler
            LOG.info("List Tracks failed", e);
            return serverError(new Error(HttpStatus.INTERNAL_SERVER_ERROR.getCode(), "List Playlists failed"));
        }
    }

    /**
     * Create a new Playlist
     */
    @Post
    @RequestBody(description = "The playlist data")
    @ApiResponse(
        responseCode = "201",
        description = "New playlist successfully created",
        content = @Content(
            schema = @Schema(implementation = Playlist.class)
        )
    )
    @ApiResponse(
        responseCode = "400",
        description = "Bad Request",
        content = @Content(
            schema = @Schema(implementation = Error.class)
        )
    )
    @ApiResponse(
        responseCode = "500",
        description = "Server Error",
        content = @Content(
            schema = @Schema(implementation = Error.class)
        )
    )
    public HttpResponse create(@Body Playlist playlist) {
        try {
            return created(playlistService.create(playlist));
        } catch (UncheckedException e) {
            // TODO: Move into ErrorHandler
            if (e.getCause() instanceof IllegalArgumentException) {
                return badRequest(new Error(HttpStatus.BAD_REQUEST.getCode(), e.getMessage()));
            }
            LOG.info("Create Playlist failed: {}", playlist, e);
            return serverError(
                    new Error(HttpStatus.INTERNAL_SERVER_ERROR.getCode(), String.format("Create Playlist failed: '%s'", 
                            playlist)));
        } catch (IllegalArgumentException e) {
            // TODO: Move into ErrorHandler
            return badRequest(new Error(HttpStatus.BAD_REQUEST.getCode(), e.getMessage()));
        } catch (Exception e) {
            // TODO: Move into ErrorHandler
            LOG.info("Create Playlist failed: {}", playlist, e);
            return serverError(
                    new Error(HttpStatus.INTERNAL_SERVER_ERROR.getCode(), String.format("Create Playlist failed: '%s'", 
                            playlist)));
        }
    }

    /**
     * Find a Playlist by name
     */
    @Get("/{name}")
    @ApiResponse(
        responseCode = "200",
        description = "Existing playlist successfully returned",
        content = @Content(
            schema = @Schema(implementation = Playlist.class)
        )
    )
    @ApiResponse(
        responseCode = "404",
        description = "Playlist not found",
        content = @Content(
            schema = @Schema(implementation = Error.class)
        )
    )
    public HttpResponse find(@Parameter(description = "The name of the playlist") String name) {
        try {
            return playlistService.findOneByName(name)
                    .map(HttpResponse::ok)
                    .orElse(notFound());
        } catch (Exception e) {
            // TODO: Move into ErrorHandler
            LOG.info("Find Playlist failed: '{}'", name, e);
            return serverError(
                    new Error(HttpStatus.INTERNAL_SERVER_ERROR.getCode(), String.format("Find Playlist failed: '%s'", 
                            name)));
        }
    }

    /**
     * Update an existing Playlist
     */
    @Put("/{name}")
    @RequestBody(description = "The playlist data")
    @ApiResponse(
        responseCode = "200",
        description = "Existing playlist successfully updated",
        content = @Content(
            schema = @Schema(implementation = Playlist.class)
        )
    )
    @ApiResponse(
        responseCode = "404",
        description = "Playlist not found",
        content = @Content(
            schema = @Schema(implementation = Error.class)
        )
    )
    @ApiResponse(
        responseCode = "500",
        description = "Server Error",
        content = @Content(
            schema = @Schema(implementation = Error.class)
        )
    )
    public HttpResponse update(
        @Parameter(description = "The name of the playlist") String name, @Body Playlist playlist) {
        try {
            return playlistService.findOneByName(name)
                    .map(ThrowingFunction.of(original -> playlistService.update(original, playlist)))
                    .map(HttpResponse::ok)
                    .orElse(notFound());
        } catch (UncheckedException e) {
            // TODO: Move into ErrorHandler
            if (e.getCause() instanceof IllegalArgumentException) {
                return badRequest(new Error(HttpStatus.BAD_REQUEST.getCode(), e.getMessage()));
            }
            LOG.info("Update Playlist failed: {}", playlist, e);
            return serverError(
                    new Error(HttpStatus.INTERNAL_SERVER_ERROR.getCode(), String.format("Update Playlist failed: '%s'", 
                            playlist)));
        } catch (IllegalArgumentException e) {
            // TODO: Move into ErrorHandler
            return badRequest(new Error(HttpStatus.BAD_REQUEST.getCode(), e.getMessage()));
        } catch (Exception e) {
            // TODO: Move into ErrorHandler
            LOG.info("Update Playlist failed: {}", playlist, e);
            return serverError(
                    new Error(HttpStatus.INTERNAL_SERVER_ERROR.getCode(), String.format("Update Playlist failed: '%s'", 
                            playlist)));
        }
    }

    /**
     * Delete a Playlist
     */
    @Delete("/{name}")
    @ApiResponse(
        responseCode = "200",
        description = "Existing playlist successfully deleted",
        content = @Content(
            schema = @Schema(implementation = Playlist.class)
        )
    )
    @ApiResponse(
        responseCode = "404",
        description = "Playlist not found",
        content = @Content(
            schema = @Schema(implementation = Error.class)
        )
    )
    @ApiResponse(
        responseCode = "500",
        description = "Server Error",
        content = @Content(
            schema = @Schema(implementation = Error.class)
        )
    )
    public HttpResponse delete(@Parameter(description = "The name of the playlist") String name) {
        try {
            return playlistService.findOneByName(name)
                    .map(ThrowingFunction.of(playlistService::delete))
                    .map(HttpResponse::ok)
                    .orElse(notFound());
        } catch (Exception e) {
            // TODO: Move into ErrorHandler
            LOG.info("Delete Playlist failed: '{}'", name, e);
            return serverError(
                    new Error(HttpStatus.INTERNAL_SERVER_ERROR.getCode(), String.format("Delete Playlist failed: '%s'",
                            name)));
        }
    }

    /**
     * List Tracks of a Playlist
     */
    @Get("/{name}/tracks{?q}")
    @ApiResponse(
        responseCode = "200",
        description = "Tracks of playlist successfully returned",
        content = @Content(
            array = @ArraySchema(
                schema = @Schema(implementation = Track.class)
            )
        )
    )
    @ApiResponse(
        responseCode = "400",
        description = "Bad Request",
        content = @Content(
            schema = @Schema(implementation = Error.class)
        )
    )
    @ApiResponse(
        responseCode = "404",
        description = "Playlist not found",
        content = @Content(
            schema = @Schema(implementation = Error.class)
        )
    )
    @ApiResponse(
        responseCode = "500",
        description = "Server Error",
        content = @Content(
            schema = @Schema(implementation = Error.class)
        )
    )
    public HttpResponse listTracks(
        @Parameter(description = "The name of the playlist") String name,
        @Parameter(description = "Filter tracks by name", required = false) Optional<String> q) {
        try {
            return playlistService.findOneByName(name)
                    .map(playlist -> q
                            .map(query -> playlistService.findTracksByTitle(playlist, query))
                            .orElse(playlist.getTracks()))
                    .map(HttpResponse::ok)
                    .orElse(notFound());
        } catch (UncheckedException e) {
            // TODO: Move into ErrorHandler
            if (e.getCause() instanceof IllegalArgumentException) {
                return badRequest(new Error(HttpStatus.BAD_REQUEST.getCode(), e.getMessage()));
            }
            LOG.info("Find Playlist failed: '{}'", name, e);
            return serverError(
                    new Error(HttpStatus.INTERNAL_SERVER_ERROR.getCode(), String.format("Find Playlist failed: '%s'",
                            name)));
        } catch (IllegalArgumentException e) {
            // TODO: Move into ErrorHandler
            return badRequest(new Error(HttpStatus.BAD_REQUEST.getCode(), e.getMessage()));
        } catch (Exception e) {
            // TODO: Move into ErrorHandler
            LOG.info("Find Playlist failed: '{}'", name, e);
            return serverError(
                    new Error(HttpStatus.INTERNAL_SERVER_ERROR.getCode(), String.format("Find Playlist failed: '%s'",
                            name)));
        }
    }

    /**
     * Create a new Track and add it to the Playlist
     */
    @Post("/{name}/tracks")
    @RequestBody(description = "The track data")
    @ApiResponse(
        responseCode = "201",
        description = "New track successfully created and added to the playlist",
        content = @Content(
            schema = @Schema(implementation = Playlist.class)
        )
    )
    @ApiResponse(
        responseCode = "400",
        description = "Bad Request",
        content = @Content(
            schema = @Schema(implementation = Error.class)
        )
    )
    @ApiResponse(
        responseCode = "404",
        description = "Playlist not found",
        content = @Content(
            schema = @Schema(implementation = Error.class)
        )
    )
    @ApiResponse(
        responseCode = "500",
        description = "Server Error",
        content = @Content(
            schema = @Schema(implementation = Error.class)
        )
    )
    public HttpResponse createTrack(
        @Parameter(description = "The name of the playlist") String name,
        @Body Track track) {
        try {
            return playlistService.findOneByName(name)
                .map(ThrowingFunction.of(p -> playlistService.addTrack(p, track)))
                .map(HttpResponse::created)
                .orElse(notFound());
        } catch (UncheckedException e) {
            // TODO: Move into ErrorHandler
            if (e.getCause() instanceof IllegalArgumentException) {
                return badRequest(new Error(HttpStatus.BAD_REQUEST.getCode(), e.getMessage()));
            }
            LOG.info("Create Track failed: {}", track, e);
            return serverError(
                    new Error(HttpStatus.INTERNAL_SERVER_ERROR.getCode(), String.format("Create Track failed: '%s'", 
                            track)));
        } catch (IllegalArgumentException e) {
            // TODO: Move into ErrorHandler
            return badRequest(new Error(HttpStatus.BAD_REQUEST.getCode(), e.getMessage()));
        } catch (Exception e) {
            // TODO: Move into ErrorHandler
            LOG.info("Create Track failed: {}", track, e);
            return serverError(
                    new Error(HttpStatus.INTERNAL_SERVER_ERROR.getCode(), String.format("Create Track failed: '%s'", 
                            track)));
        }
    }

    /**
     * Get a Track by Index
     */
    @Get("/{name}/tracks/{index}")
    @ApiResponse(
        responseCode = "200",
        description = "Track of playlist successfully returned",
        content = @Content(
            schema = @Schema(implementation = Track.class)
        )
    )
    @ApiResponse(
        responseCode = "400",
        description = "Bad Request",
        content = @Content(
            schema = @Schema(implementation = Error.class)
        )
    )
    @ApiResponse(
        responseCode = "404",
        description = "Track not found",
        content = @Content(
            schema = @Schema(implementation = Error.class)
        )
    )
    @ApiResponse(
        responseCode = "500",
        description = "Server Error",
        content = @Content(
            schema = @Schema(implementation = Error.class)
        )
    )
    public HttpResponse getTrackByIndex(
        @Parameter(description = "The name of the playlist") String name,
        @Parameter(description = "The position of the track (starting from 0)") int index) {
        try {
            final Optional<Playlist> playlist = playlistService.findOneByName(name);
            final boolean hasIndex = playlist.map(Playlist::getTracks)
                    .map(tracks -> tracks.size() > index)
                    .orElse(false);
            return playlist.isPresent() && hasIndex
                    ? ok(playlist.map(Playlist::getTracks).map(tracks -> tracks.get(index)))
                    : notFound();
        } catch (UncheckedException e) {
            // TODO: Move into ErrorHandler
            if (e.getCause() instanceof IllegalArgumentException) {
                return badRequest(new Error(HttpStatus.BAD_REQUEST.getCode(), e.getMessage()));
            }
            LOG.info("Find Playlist failed: '{}'", name, e);
            return serverError(
                    new Error(HttpStatus.INTERNAL_SERVER_ERROR.getCode(), String.format("Get Track #%d from Playlist failed: '%s'",
                            index, name)));
        } catch (IllegalArgumentException e) {
            // TODO: Move into ErrorHandler
            return badRequest(new Error(HttpStatus.BAD_REQUEST.getCode(), e.getMessage()));
        } catch (Exception e) {
            // TODO: Move into ErrorHandler
            LOG.info("Find Playlist failed: '{}'", name, e);
            return serverError(
                    new Error(HttpStatus.INTERNAL_SERVER_ERROR.getCode(), String.format("Get Track #%d from Playlist failed: '%s'",
                            index, name)));
        }
    }

    /**
     * Remove a Track from a Playlist by Index
     */
    @Delete("/{name}/tracks/{index}")
    @ApiResponse(
        responseCode = "200",
        description = "Track successfully deleted",
        content = @Content(
            schema = @Schema(implementation = Track.class)
        )
    )
    @ApiResponse(
        responseCode = "400",
        description = "Bad Request",
        content = @Content(
            schema = @Schema(implementation = Error.class)
        )
    )
    @ApiResponse(
        responseCode = "404",
        description = "Track not found",
        content = @Content(
            schema = @Schema(implementation = Error.class)
        )
    )
    @ApiResponse(
        responseCode = "500",
        description = "Server Error",
        content = @Content(
            schema = @Schema(implementation = Error.class)
        )
    )
    public HttpResponse deleteTrackByIndex(
        @Parameter(description = "The name of the playlist") String name,
        @Parameter(description = "The position of the track (starting from 0)") int index) {
        try {
            final Optional<Playlist> playlist = playlistService.findOneByName(name);
            final boolean hasIndex = playlist.map(Playlist::getTracks)
                    .map(tracks -> tracks.size() > index)
                    .orElse(false);
            return playlist.isPresent() && hasIndex
                    ? ok(playlistService.deleteTrackByIndex(playlist.get(), index))
                    : notFound();
        } catch (UncheckedException e) {
            // TODO: Move into ErrorHandler
            if (e.getCause() instanceof IllegalArgumentException) {
                return badRequest(new Error(HttpStatus.BAD_REQUEST.getCode(), e.getMessage()));
            }
            LOG.info("Find Playlist failed: '{}'", name, e);
            return serverError(
                    new Error(HttpStatus.INTERNAL_SERVER_ERROR.getCode(), String.format("Delete Track #%d Playlist failed: '%s'",
                            index, name)));
        } catch (IllegalArgumentException e) {
            // TODO: Move into ErrorHandler
            return badRequest(new Error(HttpStatus.BAD_REQUEST.getCode(), e.getMessage()));
        } catch (Exception e) {
            // TODO: Move into ErrorHandler
            LOG.info("Find Playlist failed: '{}'", name, e);
            return serverError(
                    new Error(HttpStatus.INTERNAL_SERVER_ERROR.getCode(), String.format("Delete Track #%d Playlist failed: '%s'",
                            index, name)));
        }
    }
}
