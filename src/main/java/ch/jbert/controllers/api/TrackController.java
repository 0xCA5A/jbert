package ch.jbert.controllers.api;

import ch.jbert.models.ErrorDto;
import ch.jbert.models.TrackDto;
import ch.jbert.services.TrackService;
import ch.jbert.utils.ThrowingFunction;
import ch.jbert.utils.ThrowingSupplier;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

import static io.micronaut.http.HttpResponse.badRequest;
import static io.micronaut.http.HttpResponse.created;
import static io.micronaut.http.HttpResponse.serverError;

@Controller("/api/tracks")
public class TrackController {

    private static final Logger LOG = LoggerFactory.getLogger(TrackController.class);

    @Inject
    private TrackService trackService;

    /**
     * List all available Tracks
     */
    @Get("{?q}")
    public HttpResponse list(Optional<String> q) {
        try {
            final List<TrackDto> playlists = q
                    .map(ThrowingFunction.of(n -> trackService.findAllByName(n)))
                    .orElseGet(ThrowingSupplier.of(() -> trackService.getAll()));
            return HttpResponse.ok(playlists);
        } catch (Exception e) {
            // TODO: Move into ErrorHandler
            LOG.info("List Tracks failed", e);
            return serverError(new ErrorDto(HttpStatus.INTERNAL_SERVER_ERROR.getCode(), "List Tracks failed"));
        }
    }

    /**
     * Create a new Track
     */
    @Post
    public HttpResponse create(@Body TrackDto track) {

        try {
            return created(trackService.create(track));
        } catch (IllegalArgumentException e) {
            // TODO: Move into ErrorHandler
            return badRequest(new ErrorDto(HttpStatus.BAD_REQUEST.getCode(), e.getMessage()));
        } catch (Exception e) {
            // TODO: Move into ErrorHandler
            LOG.info("Create Track failed: {}", track, e);
            return serverError(
                    new ErrorDto(HttpStatus.INTERNAL_SERVER_ERROR.getCode(), String.format("Create Track failed: '%s'", 
                            track)));
        }
    }
}
