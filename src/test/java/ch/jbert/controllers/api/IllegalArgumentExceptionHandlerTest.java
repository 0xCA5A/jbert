package ch.jbert.controllers.api;

import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;

import ch.jbert.models.Error;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.test.annotation.MicronautTest;

@MicronautTest
public class IllegalArgumentExceptionHandlerTest {

    @Inject
    private IllegalArgumentExceptionHandler illegalArgumentExceptionHandler;

    @Test
    void handle_when_ever_then_badRequest() {
        final IllegalArgumentException e = new IllegalArgumentException("foo");
        final HttpResponse response = illegalArgumentExceptionHandler.handle(HttpRequest.GET("/"), e);
        assertEquals(HttpStatus.BAD_REQUEST.getCode(), response.code());
        assertEquals(new Error(HttpStatus.BAD_REQUEST.getCode(), "foo"), response.body());
    }
}