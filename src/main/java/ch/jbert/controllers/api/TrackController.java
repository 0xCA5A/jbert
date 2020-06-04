package ch.jbert.controllers.api;

import ch.jbert.models.Error;
import ch.jbert.models.Track;
import ch.jbert.services.TrackService;
import ch.jbert.utils.ThrowingFunction;
import ch.jbert.utils.ThrowingSupplier;
import io.micronaut.http.HttpResponse;
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

import javax.inject.Inject;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static io.micronaut.http.HttpResponse.created;
import static io.micronaut.http.HttpResponse.notFound;

@Controller("/api/tracks")
@Tag(name = "${api.tags.tracks.name}")
public class TrackController {

    @Inject
    private TrackService trackService;

    /**
     * List all available Tracks
     */
    @Get("{?q}")
    @ApiResponse(responseCode = "200", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Track.class))))
    @ApiResponse(responseCode = "500", description = "Server Error", content = @Content(schema = @Schema(implementation = Error.class)))
    public HttpResponse list(@Parameter(description = "Filter tracks by name", required = false) Optional<String> q) {
        final List<Track> playlists = q.map(ThrowingFunction.of(n -> trackService.findAllByName(n)))
                .orElseGet(ThrowingSupplier.of(() -> trackService.getAll()));
        return HttpResponse.ok(playlists);
    }

    /**
     * Create a new Track
     */
    @Post
    @RequestBody(description = "The track data")
    @ApiResponse(responseCode = "201", description = "New track successfully created", content = @Content(schema = @Schema(implementation = Track.class)))
    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Error.class)))
    @ApiResponse(responseCode = "500", description = "Server Error", content = @Content(schema = @Schema(implementation = Error.class)))
    public HttpResponse create(@Body Track track) {
        return created(trackService.create(track));
    }

    /**
     * Find a Track by hash
     */
    @Get("/{hash}")
    @ApiResponse(responseCode = "200", description = "Existing track successfully returned", content = @Content(schema = @Schema(implementation = Track.class)))
    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Error.class)))
    @ApiResponse(responseCode = "404", description = "Track not found", content = @Content(schema = @Schema(implementation = Error.class)))
    public HttpResponse find(@Parameter(description = "The hash of the track") String hash) throws IOException {
        return trackService.findOneByHash(hash)
                .map(HttpResponse::ok)
                .orElse(notFound());
    }

    /**
     * Update a Track
     */
    @Put("/{hash}")
    @ApiResponse(responseCode = "200", description = "Existing track successfully updated", content = @Content(schema = @Schema(implementation = Track.class)))
    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Error.class)))
    @ApiResponse(responseCode = "404", description = "Track not found", content = @Content(schema = @Schema(implementation = Error.class)))
    @ApiResponse(responseCode = "500", description = "Server Error", content = @Content(schema = @Schema(implementation = Error.class)))
    public HttpResponse update(@Parameter(description = "The hash of the track") String hash, @Body Track track)
    throws IOException {
        return trackService.findOneByHash(hash)
                .map(original -> trackService.update(original, track))
                .map(HttpResponse::ok)
                .orElse(notFound());
    }

    /**
     * Delete a Track
     */
    @Delete("/{hash}")
    @ApiResponse(responseCode = "200", description = "Existing track successfully deleted", content = @Content(schema = @Schema(implementation = Track.class)))
    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Error.class)))
    @ApiResponse(responseCode = "404", description = "Track not found", content = @Content(schema = @Schema(implementation = Error.class)))
    @ApiResponse(responseCode = "500", description = "Server Error", content = @Content(schema = @Schema(implementation = Error.class)))
    public HttpResponse delete(@Parameter(description = "The hash of the track") String hash) throws IOException {
        return trackService.findOneByHash(hash)
                .map(trackService::delete)
                .map(HttpResponse::ok)
                .orElse(notFound());
    }
}
