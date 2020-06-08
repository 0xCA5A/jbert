package ch.jbert.controllers.api;

import java.io.IOException;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

import ch.jbert.models.Error;
import ch.jbert.services.TrackService;
import ch.jbert.utils.UncheckedException;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.annotation.MicronautTest;
import io.micronaut.test.annotation.MockBean;

@MicronautTest
class ExceptionHandlingTest {

    @Inject
    TrackService trackService;

    @Inject
    @Client("/api")
    RxHttpClient client;

    @MockBean(TrackService.class)
    TrackService trackService() {
        TrackService mock = mock(TrackService.class);
        return mock;
    }

    @Test
    void handle_when_illegalArgumentException_then_badRequest() throws IOException {

        when( trackService.getAll() )
                .thenThrow(IllegalArgumentException.class);

        try {
                client.toBlocking()
                        .exchange(HttpRequest.GET("/tracks"), Error.class);
                fail();
        } catch (HttpClientResponseException e) {
                final HttpResponse<Error> response = (HttpResponse<Error>) e.getResponse();
                assertEquals(HttpStatus.BAD_REQUEST.getCode(), response.code());
                assertFalse(response.getBody().isPresent());
        }
    }

    @Test
    void handle_when_uncheckedException_then_badRequest() throws IOException {

        when( trackService.getAll() )
                .thenThrow(UncheckedException.wrapThrowable(new IllegalArgumentException("foo")));

        try {
                client.toBlocking()
                        .exchange(HttpRequest.GET("/tracks"), Error.class);
                fail();
        } catch (HttpClientResponseException e) {
                final HttpResponse<Error> response = (HttpResponse<Error>) e.getResponse();
                assertEquals(HttpStatus.BAD_REQUEST.getCode(), response.code());
                assertFalse(response.getBody().isPresent());
        }
    }

    @Test
    void handle_when_uncheckedException_then_serverError() throws IOException {

        when( trackService.getAll() )
                .thenThrow(UncheckedException.wrapThrowable(new Exception("foo")));

        try {
                client.toBlocking()
                        .exchange(HttpRequest.GET("/tracks"), Error.class);
                fail();
        } catch (HttpClientResponseException e) {
                final HttpResponse<Error> response = (HttpResponse<Error>) e.getResponse();
                assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.getCode(), response.code());
                assertFalse(response.getBody().isPresent());
        }
    }

    @Test
    void handle_when_ioException_then_serverError() throws IOException {

        when( trackService.getAll() )
                .thenThrow(IOException.class);

        try {
                client.toBlocking()
                        .exchange(HttpRequest.GET("/tracks"), Error.class);
                fail();
        } catch (HttpClientResponseException e) {
                final HttpResponse<Error> response = (HttpResponse<Error>) e.getResponse();
                assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.getCode(), response.code());
                assertFalse(response.getBody().isPresent());
        }
    }

    @Test
    void handle_when_genericException_then_serverError() throws IOException {

        when( trackService.getAll() )
                .thenThrow(RuntimeException.class);

        try {
                client.toBlocking()
                        .exchange(HttpRequest.GET("/tracks"), Error.class);
                fail();
        } catch (HttpClientResponseException e) {
                final HttpResponse<Error> response = (HttpResponse<Error>) e.getResponse();
                assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.getCode(), response.code());
                assertFalse(response.getBody().isPresent());
        }
    }
}
