package ch.jbert.controllers.api;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

import ch.jbert.models.Error;
import ch.jbert.models.Metadata;
import ch.jbert.models.Track;
import ch.jbert.services.TrackService;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.annotation.MicronautTest;
import io.micronaut.test.annotation.MockBean;

@MicronautTest
class TrackControllerTest {

    private static final Track track1 = new Track(Metadata.newBuilder()
            .withArtist("Alice Cooper")
            .withAlbum("Trash")
            .withTitle("Poison")
            .build());
    private static final Track track2 = new Track(Metadata.newBuilder()
            .withArtist("Bee Gees")
            .withAlbum("Greatest")
            .withTitle("Night Fever")
            .build());
    private static final List<Track> ALL_TRACKS = Arrays.asList(track1, track2);

    @Inject
    TrackService trackService;

    @Inject
    @Client("/api/tracks")
    RxHttpClient client;

    @MockBean(TrackService.class)
    TrackService trackService() {
        TrackService mock = mock(TrackService.class);
        return mock;
    }

    @Test
    void list_when_withoutQuery_then_returnAll() throws IOException {

        when( trackService.getAll() )
                .then(invocation -> ALL_TRACKS);
        
        final HttpResponse<List<Track>> response = client.toBlocking()
                .exchange(HttpRequest.GET(""), Argument.listOf(Track.class));

        assertEquals(HttpStatus.OK.getCode(), response.code());
        assertEquals(ALL_TRACKS, response.body());

        verify(trackService).getAll();
    }

    @Test
    void list_when_withQuery_then_findAllByName() throws IOException {

        final String query = "poison";

        when( trackService.findAllByName(query) )
                .then(invocation -> Arrays.asList(track1));

        final HttpResponse<List<Track>> response = client.toBlocking()
                .exchange(HttpRequest.GET("?q=" + query), Argument.listOf(Track.class));

        assertEquals(HttpStatus.OK.getCode(), response.code());
        assertEquals(Collections.singletonList(track1), response.body());

        verify(trackService).findAllByName(query);
    }

    @Test
    void list_when_withQuery_then_findNone() throws IOException {

        final String query = "nonexisting";

        when( trackService.findAllByName(query) )
                .then(invocation -> Collections.EMPTY_LIST);

        final HttpResponse<List<Track>> response = client.toBlocking()
                .exchange(HttpRequest.GET("?q=" + query), Argument.listOf(Track.class));

        assertEquals(HttpStatus.OK.getCode(), response.code());
        assertEquals(Collections.EMPTY_LIST, response.body());

        verify(trackService).findAllByName(query);
    }

    @Test
    void create_when_successful_then_returnCreatedTrack() throws IOException {

        when( trackService.create(any()) )
                .then(invocation -> track1);
        
        final HttpResponse<Track> response = client.toBlocking()
                .exchange(HttpRequest.POST("", track1), Track.class);

        assertEquals(HttpStatus.CREATED.getCode(), response.code());
        assertEquals(track1, response.body());

        verify(trackService).create(any());
    }

    @Test
    void find_when_existingHash_then_returnTrack() throws IOException {
        
        final String hash = track1.calculateSha256();

        when( trackService.findOneByHash(hash) )
                .then(invocation -> Optional.of(track1));
        
        final HttpResponse<Track> response = client.toBlocking()
                .exchange(HttpRequest.GET("/" + hash), Track.class);
        
        assertEquals(HttpStatus.OK.getCode(), response.code());
        assertEquals(track1, response.body());

        verify(trackService).findOneByHash(hash);
    }

    @Test
    void find_when_nonexistingHash_then_notFound() throws IOException {
        
        final String hash = track1.calculateSha256();

        when( trackService.findOneByHash(hash) )
                .then(invocation -> Optional.empty());
        
        try {
                client.toBlocking()
                        .exchange(HttpRequest.GET("/" + hash), Track.class);
                fail();
        } catch (HttpClientResponseException e) {
                final HttpResponse<Error> response = (HttpResponse<Error>) e.getResponse();
                assertEquals(HttpStatus.NOT_FOUND.getCode(), response.code());
                assertFalse(response.getBody().isPresent());
        }

        verify(trackService).findOneByHash(hash);
    }

    @Test
    void update_when_existingHash_then_returnUpdatedTrack() throws IOException {
        
        final String hash = track1.calculateSha256();

        when( trackService.findOneByHash(hash) )
                .then(invocation -> Optional.of(track1));
        when( trackService.update(track1, track2) )
                .then(invocation -> track2);
        
        final HttpResponse<Track> response = client.toBlocking()
                .exchange(HttpRequest.PUT("/" + hash, track2), Track.class);
        
        assertEquals(HttpStatus.OK.getCode(), response.code());
        assertEquals(track2, response.body());

        verify(trackService).findOneByHash(hash);
        verify(trackService).update(track1, track2);
    }

    @Test
    void update_when_nonexistingHash_then_notFound() throws IOException {
        
        final String hash = track1.calculateSha256();

        when( trackService.findOneByHash(hash) )
                .then(invocation -> Optional.empty());
        
        try {
                client.toBlocking()
                        .exchange(HttpRequest.PUT("/" + hash, track2), Track.class);
                fail();
        } catch (HttpClientResponseException e) {
                final HttpResponse<Error> response = (HttpResponse<Error>) e.getResponse();
                assertEquals(HttpStatus.NOT_FOUND.getCode(), response.code());
                assertFalse(response.getBody().isPresent());
        }

        verify(trackService).findOneByHash(hash);
    }

    @Test
    void delete_when_existingHash_then_returnDeletedTrack() throws IOException {
        
        final String hash = track1.calculateSha256();

        when( trackService.findOneByHash(hash) )
                .then(invocation -> Optional.of(track1));
        when( trackService.delete(track1) )
                .then(invocation -> track1);
        
        final HttpResponse<Track> response = client.toBlocking()
                .exchange(HttpRequest.DELETE("/" + hash), Track.class);
        
        assertEquals(HttpStatus.OK.getCode(), response.code());
        assertEquals(track1, response.body());

        verify(trackService).findOneByHash(hash);
        verify(trackService).delete(track1);
    }

    @Test
    void delete_when_nonexistingHash_then_notFound() throws IOException {
        
        final String hash = track1.calculateSha256();

        when( trackService.findOneByHash(hash) )
                .then(invocation -> Optional.empty());
        
        try {
                client.toBlocking()
                        .exchange(HttpRequest.DELETE("/" + hash), Track.class);
                fail();
        } catch (HttpClientResponseException e) {
                final HttpResponse<Error> response = (HttpResponse<Error>) e.getResponse();
                assertEquals(HttpStatus.NOT_FOUND.getCode(), response.code());
                assertFalse(response.getBody().isPresent());
        }

        verify(trackService).findOneByHash(hash);
    }
}
