package ch.jbert.controllers.api;

import ch.jbert.models.Error;
import ch.jbert.models.Track;
import ch.jbert.services.TrackService;
import ch.jbert.utils.ThrowingFunction;
import ch.jbert.utils.ThrowingSupplier;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

import static io.micronaut.http.HttpResponse.badRequest;
import static io.micronaut.http.HttpResponse.created;
import static io.micronaut.http.HttpResponse.serverError;

@Controller("/api/tracks")
@Tag(name = "${api.tags.tracks.name}")
public class TrackController {

    private static final Logger LOG = LoggerFactory.getLogger(TrackController.class);

    @Inject
    private TrackService trackService;

    /**
     * List all available Tracks
     */
    @Get("{?q}")
    @ApiResponse(
        responseCode = "200",
        content = @Content(
            array = @ArraySchema(
                schema = @Schema(implementation = Track.class)
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
        @Parameter(description = "Filter tracks by metadata attributes", required = false) Optional<String> q) {
        try {
            final List<Track> playlists = q
                    .map(ThrowingFunction.of(n -> trackService.findAllByName(n)))
                    .orElseGet(ThrowingSupplier.of(() -> trackService.getAll()));
            return HttpResponse.ok(playlists);
        } catch (Exception e) {
            // TODO: Move into ErrorHandler
            LOG.info("List Tracks failed", e);
            return serverError(new Error(HttpStatus.INTERNAL_SERVER_ERROR.getCode(), "List Tracks failed"));
        }
    }

    /**
     * Create a new Track
     */
    @Post
    @RequestBody(description = "The track data")
    @ApiResponse(
        responseCode = "201",
        description = "New track successfully created",
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
        responseCode = "500",
        description = "Server Error",
        content = @Content(
            schema = @Schema(implementation = Error.class)
        )
    )
    public HttpResponse create(@Body Track track) {
        try {
            return created(trackService.create(track));
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
}
