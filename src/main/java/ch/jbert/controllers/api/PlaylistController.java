package ch.jbert.controllers.api;

import ch.jbert.models.Error;
import ch.jbert.models.Playlist;
import ch.jbert.models.Track;
import ch.jbert.services.PlaylistService;
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

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import static io.micronaut.http.HttpResponse.created;
import static io.micronaut.http.HttpResponse.notFound;
import static io.micronaut.http.HttpResponse.ok;

@Controller("/api/playlists")
@Tag(name = "${api.tags.playlists.name}")
public class PlaylistController {

    @Inject
    private PlaylistService playlistService;

    /**
     * List all available Playlists
     */
    @Get("{?q}")
    @ApiResponse(responseCode = "200", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Playlist.class))))
    @ApiResponse(responseCode = "500", description = "Server Error", content = @Content(schema = @Schema(implementation = Error.class)))
    public HttpResponse list(
        @Parameter(description = "Filter playlists by name", required = false) Optional<String> q) {
        final List<Playlist> playlists = q
            .map(ThrowingFunction.of(n -> playlistService.findAllByName(n)))
            .orElseGet(ThrowingSupplier.of(() -> playlistService.getAll()));
        return ok(playlists);
    }

    /**
     * Create a new Playlist
     */
    @Post
    @RequestBody(description = "The playlist data")
    @ApiResponse(responseCode = "201", description = "New playlist successfully created", content = @Content(schema = @Schema(implementation = Playlist.class)))
    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Error.class)))
    @ApiResponse(responseCode = "500", description = "Server Error", content = @Content(schema = @Schema(implementation = Error.class)))
    public HttpResponse create(@Body Playlist playlist) throws IOException {
        return created(playlistService.create(playlist));
    }

    /**
     * Find a Playlist by name
     */
    @Get("/{name}")
    @ApiResponse(responseCode = "200", description = "Existing playlist successfully returned", content = @Content(schema = @Schema(implementation = Playlist.class)))
    @ApiResponse(responseCode = "404", description = "Playlist not found", content = @Content(schema = @Schema(implementation = Error.class)))
    public HttpResponse find(@Parameter(description = "The name of the playlist") String name) {
        return playlistService.findOneByName(name)
                .map(HttpResponse::ok)
                .orElse(notFound());
    }

    /**
     * Update an existing Playlist
     */
    @Put("/{name}")
    @RequestBody(description = "The playlist data")
    @ApiResponse(responseCode = "200", description = "Existing playlist successfully updated", content = @Content(schema = @Schema(implementation = Playlist.class)))
    @ApiResponse(responseCode = "404", description = "Playlist not found", content = @Content(schema = @Schema(implementation = Error.class)))
    @ApiResponse(responseCode = "500", description = "Server Error", content = @Content(schema = @Schema(implementation = Error.class)))
    public HttpResponse update(
        @Parameter(description = "The name of the playlist") String name, @Body Playlist playlist) {
        return playlistService.findOneByName(name)
                .map(ThrowingFunction.of(original -> playlistService.update(original, playlist)))
                .map(HttpResponse::ok)
                .orElse(notFound());
    }

    /**
     * Delete a Playlist
     */
    @Delete("/{name}")
    @ApiResponse(responseCode = "200", description = "Existing playlist successfully deleted", content = @Content(schema = @Schema(implementation = Playlist.class)))
    @ApiResponse(responseCode = "404", description = "Playlist not found", content = @Content(schema = @Schema(implementation = Error.class)))
    @ApiResponse(responseCode = "500", description = "Server Error", content = @Content(schema = @Schema(implementation = Error.class)))
    public HttpResponse delete(@Parameter(description = "The name of the playlist") String name) {
        return playlistService.findOneByName(name)
                .map(ThrowingFunction.of(playlistService::delete))
                .map(HttpResponse::ok)
                .orElse(notFound());
    }

    /**
     * List Tracks of a Playlist
     */
    @Get("/{name}/tracks{?q}")
    @ApiResponse(responseCode = "200", description = "Tracks of playlist successfully returned", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Track.class))))
    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Error.class)))
    @ApiResponse(responseCode = "404", description = "Playlist not found", content = @Content(schema = @Schema(implementation = Error.class)))
    @ApiResponse(responseCode = "500", description = "Server Error", content = @Content(schema = @Schema(implementation = Error.class)))
    public HttpResponse listTracks(@Parameter(description = "The name of the playlist") String name,
                                   @Parameter(description = "Filter tracks by name", required = false) Optional<String> q) {
        return playlistService.findOneByName(name)
                .map(playlist -> q
                        .map(query -> playlistService.findTracksByTitle(playlist, query))
                        .orElse(playlist.getTracks()))
                .map(HttpResponse::ok)
                .orElse(notFound());
    }

    /**
     * Create a new Track and add it to the Playlist
     */
    @Post("/{name}/tracks")
    @RequestBody(description = "The track data")
    @ApiResponse(responseCode = "201", description = "New track successfully created and added to the playlist", content = @Content(schema = @Schema(implementation = Playlist.class)))
    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Error.class)))
    @ApiResponse(responseCode = "404", description = "Playlist not found", content = @Content(schema = @Schema(implementation = Error.class)))
    @ApiResponse(responseCode = "500", description = "Server Error", content = @Content(schema = @Schema(implementation = Error.class)))
    public HttpResponse createTrack(@Parameter(description = "The name of the playlist") String name,
                                    @Body Track track) {
        return playlistService.findOneByName(name)
            .map(ThrowingFunction.of(p -> playlistService.addTrack(p, track)))
            .map(HttpResponse::created)
            .orElse(notFound());
    }

    /**
     * Get a Track by Index
     */
    @Get("/{name}/tracks/{index}")
    @ApiResponse(responseCode = "200", description = "Track of playlist successfully returned", content = @Content(schema = @Schema(implementation = Track.class)))
    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Error.class)))
    @ApiResponse(responseCode = "404", description = "Track not found", content = @Content(schema = @Schema(implementation = Error.class)))
    @ApiResponse(responseCode = "500", description = "Server Error", content = @Content(schema = @Schema(implementation = Error.class)))
    public HttpResponse getTrackByIndex(@Parameter(description = "The name of the playlist") String name,
                                        @Parameter(description = "The position of the track (starting from 0)") int index) {
        final Optional<Playlist> playlist = playlistService.findOneByName(name);
        final boolean hasIndex = playlist.map(Playlist::getTracks)
                .map(tracks -> tracks.size() > index)
                .orElse(false);
        return playlist.isPresent() && hasIndex
                ? ok(playlist.map(Playlist::getTracks).map(tracks -> tracks.get(index)))
                : notFound();
    }

    /**
     * Remove a Track from a Playlist by Index
     */
    @Delete("/{name}/tracks/{index}")
    @ApiResponse(responseCode = "200", description = "Track successfully deleted", content = @Content(schema = @Schema(implementation = Track.class)))
    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Error.class)))
    @ApiResponse(responseCode = "404", description = "Track not found", content = @Content(schema = @Schema(implementation = Error.class)))
    @ApiResponse(responseCode = "500", description = "Server Error", content = @Content(schema = @Schema(implementation = Error.class)))
    public HttpResponse deleteTrackByIndex(@Parameter(description = "The name of the playlist") String name,
                                           @Parameter(description = "The position of the track (starting from 0)") int index) throws IOException {
        final Optional<Playlist> playlist = playlistService.findOneByName(name);
        final boolean hasIndex = playlist.map(Playlist::getTracks)
                .map(tracks -> tracks.size() > index)
                .orElse(false);
        return playlist.isPresent() && hasIndex
                ? ok(playlistService.deleteTrackByIndex(playlist.get(), index))
                : notFound();
    }
}
