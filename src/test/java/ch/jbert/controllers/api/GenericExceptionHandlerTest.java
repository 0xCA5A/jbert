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
public class GenericExceptionHandlerTest {

    @Inject
    private GenericExceptionHandler genericExceptionHandler;

    @Test
    void handle_when_ever_then_badRequest() {
        final Exception e = new Exception("foo");
        final HttpResponse response = genericExceptionHandler.handle(HttpRequest.GET("/"), e);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.getCode(), response.code());
        assertEquals(new Error(HttpStatus.INTERNAL_SERVER_ERROR.getCode(), "foo"), response.body());
    }
}