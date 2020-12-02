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

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import ch.jbert.models.Error;
import ch.jbert.models.Metadata;
import ch.jbert.models.Playlist;
import ch.jbert.models.Track;
import ch.jbert.services.PlaylistService;
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
class PlaylistControllerTest {

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
    private static final Playlist playlist1 = new Playlist("playlist1", Collections.singletonList(track1));
    private static final Playlist playlist2 = new Playlist("playlist2", Collections.singletonList(track2));
    private static final List<Playlist> ALL_PLAYLISTS = Arrays.asList(playlist1, playlist2);
            
    @Inject
    PlaylistService playlistService;

    @Inject
    @Client("/api/playlists")
    RxHttpClient client;

    @MockBean(PlaylistService.class)
    PlaylistService playlistService() {
        PlaylistService mock = mock(PlaylistService.class);
        return mock;
    }

    @Test
    void list_when_withoutQuery_then_returnAll() throws IOException {

        when( playlistService.getAll() )
                .then(invocation -> ALL_PLAYLISTS);
        
        final HttpResponse<List<Playlist>> response = client.toBlocking()
                .exchange(HttpRequest.GET(""), Argument.listOf(Playlist.class));

        assertEquals(HttpStatus.OK.getCode(), response.code());
        assertEquals(ALL_PLAYLISTS, response.body());

        verify(playlistService).getAll();
    }

    @Test
    void list_when_withQuery_then_findAllByName() throws IOException {

        final String query = "list1";

        when( playlistService.findAllByName(query) )
                .then(invocation -> Arrays.asList(playlist1));

        final HttpResponse<List<Playlist>> response = client.toBlocking()
                .exchange(HttpRequest.GET("?q=" + query), Argument.listOf(Playlist.class));

        assertEquals(HttpStatus.OK.getCode(), response.code());
        assertEquals(Collections.singletonList(playlist1), response.body());

        verify(playlistService).findAllByName(query);
    }

    @Test
    void list_when_withQuery_then_findNone() throws IOException {

        final String query = "nonexisting";

        when( playlistService.findAllByName(query) )
                .then(invocation -> Collections.EMPTY_LIST);

        final HttpResponse<List<Playlist>> response = client.toBlocking()
                .exchange(HttpRequest.GET("?q=" + query), Argument.listOf(Playlist.class));

        assertEquals(HttpStatus.OK.getCode(), response.code());
        assertEquals(Collections.EMPTY_LIST, response.body());

        verify(playlistService).findAllByName(query);
    }

    @Test
    void create_when_successful_then_returnCreatedPlaylist() throws IOException {

        when( playlistService.create(any()) )
                .then(invocation -> playlist1);

        final HttpResponse<Playlist> response = client.toBlocking()
                .exchange(HttpRequest.POST("", playlist1), Playlist.class);

        assertEquals(HttpStatus.CREATED.getCode(), response.code());
        assertEquals(playlist1, response.body());

        verify(playlistService).create(any());
    }

    @Test
    void find_when_existing_then_returnPlaylist() throws IOException {

        final String name = "playlist1";

        when( playlistService.findOneByName(name) )
                .then(invocation -> Optional.of(playlist1));

        final HttpResponse<Playlist> response = client.toBlocking()
                .exchange(HttpRequest.GET("/" + name), Playlist.class);
        
        assertEquals(HttpStatus.OK.getCode(), response.code());
        assertEquals(playlist1, response.body());

        verify(playlistService).findOneByName(name);
    }

    @Test
    void find_when_nonexisting_then_notFound() throws IOException {
        
        final String name = "nonexisting";

        when( playlistService.findOneByName(name) )
                .then(invocation -> Optional.empty());

        try {
                client.toBlocking()
                        .exchange(HttpRequest.GET("/" + name), Playlist.class);
                fail();
        } catch (HttpClientResponseException e) {
                final HttpResponse<Error> response = (HttpResponse<Error>) e.getResponse();
                assertEquals(HttpStatus.NOT_FOUND.getCode(), response.code());
                assertFalse(response.getBody().isPresent());
        }

        verify(playlistService).findOneByName(name);
    }

    @Test
    void update_when_existing_then_returnUpdatedPlaylist() throws IOException {
        
        final String name = "playlist1";

        when( playlistService.findOneByName(name) )
                .then(invocation -> Optional.of(playlist1));
        when( playlistService.update(playlist1, playlist2) )
                .then(invocation -> playlist2);

        final HttpResponse<Playlist> response = client.toBlocking()
                .exchange(HttpRequest.PUT("/" + name, playlist2), Playlist.class);
        
        assertEquals(HttpStatus.OK.getCode(), response.code());
        assertEquals(playlist2, response.body());

        verify(playlistService).findOneByName(name);
        verify(playlistService).update(playlist1, playlist2);
    }

    @Test
    void update_when_nonexisting_then_notFound() throws IOException {
        
        final String name = "playlist1";

        when( playlistService.findOneByName(name) )
                .then(invocation -> Optional.empty());

        try {
                client.toBlocking()
                        .exchange(HttpRequest.PUT("/" + name, playlist2), Track.class);
                fail();
        } catch (HttpClientResponseException e) {
                final HttpResponse<Error> response = (HttpResponse<Error>) e.getResponse();
                assertEquals(HttpStatus.NOT_FOUND.getCode(), response.code());
                assertFalse(response.getBody().isPresent());
        }

        verify(playlistService).findOneByName(name);
    }

    @Test
    void delete_when_existing_then_returnDeletedPlaylist() throws IOException {
        
        final String name = "playlist1";

        when( playlistService.findOneByName(name) )
                .then(invocation -> Optional.of(playlist1));
        when( playlistService.delete(playlist1) )
                .then(invocation -> playlist1);
        
        final HttpResponse<Playlist> response = client.toBlocking()
                .exchange(HttpRequest.DELETE("/" + name), Playlist.class);

        assertEquals(HttpStatus.OK.getCode(), response.code());
        assertEquals(playlist1, response.body());

        verify(playlistService).findOneByName(name);
        verify(playlistService).delete(playlist1);
    }

    @Test
    void delete_when_nonexisting_then_notFound() throws IOException {
        
        final String name = "playlist1";

        when( playlistService.findOneByName(name) )
                .then(invocation -> Optional.empty());

        try {
                client.toBlocking()
                        .exchange(HttpRequest.DELETE("/" + name), Playlist.class);
                fail();
        } catch (HttpClientResponseException e) {
                final HttpResponse<Error> response = (HttpResponse<Error>) e.getResponse();
                assertEquals(HttpStatus.NOT_FOUND.getCode(), response.code());
                assertFalse(response.getBody().isPresent());
        }

        verify(playlistService).findOneByName(name);
    }

    @Test
    void listTracks_when_existingPlaylistWithoutQuery_then_returnAllTracksOfPlaylist() {

        final String name = "playlist1";

        when( playlistService.findOneByName(name) )
                .then(invocation -> Optional.of(playlist1));

        final HttpResponse<List<Track>> response = client.toBlocking()
                .exchange(HttpRequest.GET("/" + name + "/tracks"), Argument.listOf(Track.class));

        assertEquals(HttpStatus.OK.getCode(), response.code());
        assertEquals(playlist1.getTracks(), response.body());

        verify(playlistService).findOneByName(name);
    }

    @Test
    void listTracks_when_existingPlaylistWithQuery_then_returnAllTracksOfPlaylistByName() throws IOException {

        final String name = "playlist1";
        final String query = "poison";

        when( playlistService.findOneByName(name) )
                .then(invocation -> Optional.of(playlist1));

        final HttpResponse<List<Track>> response = client.toBlocking()
                .exchange(HttpRequest.GET("/" + name + "/tracks?q=" + query), Argument.listOf(Track.class));

        assertEquals(HttpStatus.OK.getCode(), response.code());
        assertEquals(Collections.singletonList(track1), response.body());

        verify(playlistService).findOneByName(name);
    }

    @Test
    void listTracks_when_existingPlaylistWithQuery_then_findNone() throws IOException {

        final String name = "playlist1";
        final String query = "nonexisting";

        when( playlistService.findOneByName(name) )
                .then(invocation -> Optional.of(playlist1));

        final HttpResponse<List<Track>> response = client.toBlocking()
                .exchange(HttpRequest.GET("/" + name + "/tracks?q=" + query), Argument.listOf(Track.class));

        assertEquals(HttpStatus.OK.getCode(), response.code());
        assertEquals(Collections.EMPTY_LIST, response.body());

        verify(playlistService).findOneByName(name);
    }

    @Test
    void listTracks_when_nonexistingPlaylist_then_notFound() {

        final String name = "playlist1";

        when( playlistService.findOneByName(name) )
                .then(invocation -> Optional.empty());

        try {
                client.toBlocking()
                        .exchange(HttpRequest.GET("/" + name), Playlist.class);
                fail();
        } catch (HttpClientResponseException e) {
                final HttpResponse<Error> response = (HttpResponse<Error>) e.getResponse();
                assertEquals(HttpStatus.NOT_FOUND.getCode(), response.code());
                assertFalse(response.getBody().isPresent());
        }

        verify(playlistService).findOneByName(name);
    }

    @Test
    void createTrack_when_existingPlaylist_then_returnUpdatedPlaylist() throws IOException {

        final String name = "playlist1";
        final Playlist updatedPlaylist = playlist1.getBuilder()
                .withTracks(Arrays.asList(track1, track2))
                .build();

        when( playlistService.findOneByName(name) )
                .then(invocation -> Optional.of(playlist1));
        when( playlistService.addTrack(playlist1, track2))
                .then(invocation -> updatedPlaylist);

        final HttpResponse<Playlist> response = client.toBlocking()
                .exchange(HttpRequest.POST("/" + name + "/tracks", track2), Playlist.class);

        assertEquals(HttpStatus.CREATED.getCode(), response.code());
        assertEquals(updatedPlaylist, response.body());

        verify(playlistService).findOneByName(name);
        verify(playlistService).addTrack(playlist1, track2);
    }

    @Test
    void createTrack_when_nonexistingPlaylist_then_notFound() throws IOException {

        final String name = "nonexisting";

        when( playlistService.findOneByName(name) )
                .then(invocation -> Optional.empty());

        try {
                client.toBlocking()
                        .exchange(HttpRequest.POST("/" + name + "/tracks", track2), Playlist.class);
                fail();
        } catch (HttpClientResponseException e) {
                final HttpResponse<Error> response = (HttpResponse<Error>) e.getResponse();
                assertEquals(HttpStatus.NOT_FOUND.getCode(), response.code());
                assertFalse(response.getBody().isPresent());
        }

        verify(playlistService).findOneByName(name);
    }

    @Test
    void getTrackByIndex_when_existing_then_returnTrack() {

        final String name = "playlist1";

        when( playlistService.findOneByName(name) )
                .then(invocation -> Optional.of(playlist1));

        // assertEquals(track1, playlist1.getTracks().get(0));

        final HttpResponse<Track> response = client.toBlocking()
                .exchange(HttpRequest.GET("/" + name + "/tracks/" + 0));

        assertEquals(HttpStatus.OK.getCode(), response.code());
        assertEquals(track1, response.body());

        verify(playlistService).findOneByName(name);
    }

    @Test
    void getTrackByIndex_when_nonexistingPlaylist_then_notFound() {

        final String name = "playlist1";

        when( playlistService.findOneByName(name) )
                .then(invocation -> Optional.empty());

        try {
                client.toBlocking()
                        .exchange(HttpRequest.GET("/" + name + "/tracks/" + 0));
                fail();
        } catch (HttpClientResponseException e) {
                final HttpResponse<Error> response = (HttpResponse<Error>) e.getResponse();
                assertEquals(HttpStatus.NOT_FOUND.getCode(), response.code());
                assertFalse(response.getBody().isPresent());
        }

        verify(playlistService).findOneByName(name);
    }

    @Test
    void getTrackByIndex_when_nonexistingTrack_then_notFound() {

        final String name = "playlist1";

        when( playlistService.findOneByName(name) )
                .then(invocation -> Optional.empty());

        try {
                client.toBlocking()
                        .exchange(HttpRequest.GET("/" + name + "/tracks/" + playlist1.getTracks().size()));
                fail();
        } catch (HttpClientResponseException e) {
                final HttpResponse<Error> response = (HttpResponse<Error>) e.getResponse();
                assertEquals(HttpStatus.NOT_FOUND.getCode(), response.code());
                assertFalse(response.getBody().isPresent());
        }

        verify(playlistService).findOneByName(name);
    }

    @Test
    void deleteTrackByIndex_when_existing_then_returnUpdatedPlaylist() throws IOException {

        final String name = "playlist1";
        final Playlist updatedPlaylist = playlist1.getBuilder()
                .withTracks(Collections.emptyList())
                .build();

        when( playlistService.findOneByName(name) )
                .then(invocation -> Optional.of(playlist1));
        when( playlistService.deleteTrackByIndex(playlist1, 0))
                .then(invocation -> updatedPlaylist);

        final HttpResponse<Playlist> response = client.toBlocking()
                .exchange(HttpRequest.DELETE("/" + name + "/tracks/" + 0));

        assertEquals(HttpStatus.OK.getCode(), response.code());
        assertEquals(updatedPlaylist, response.body());

        verify(playlistService).findOneByName(name);
        verify(playlistService).deleteTrackByIndex(playlist1, 0);
    }

    @Test
    void deleteTrackByIndex_when_nonexistingPlaylist_then_notFound() {

        final String name = "playlist1";

        when( playlistService.findOneByName(name) )
                .then(invocation -> Optional.empty());

        try {
                client.toBlocking()
                        .exchange(HttpRequest.DELETE("/" + name + "/tracks/" + 0));
                fail();
        } catch (HttpClientResponseException e) {
                final HttpResponse<Error> response = (HttpResponse<Error>) e.getResponse();
                assertEquals(HttpStatus.NOT_FOUND.getCode(), response.code());
                assertFalse(response.getBody().isPresent());
        }

        verify(playlistService).findOneByName(name);
    }

    @Test
    void deleteTrackByIndex_when_nonexistingTrack_then_notFound() {

        final String name = "playlist1";

        when( playlistService.findOneByName(name) )
                .then(invocation -> Optional.empty());

        try {
                client.toBlocking()
                        .exchange(HttpRequest.DELETE("/" + name + "/tracks/" + playlist1.getTracks().size()));
                fail();
        } catch (HttpClientResponseException e) {
                final HttpResponse<Error> response = (HttpResponse<Error>) e.getResponse();
                assertEquals(HttpStatus.NOT_FOUND.getCode(), response.code());
                assertFalse(response.getBody().isPresent());
        }

        verify(playlistService).findOneByName(name);
    }
}
