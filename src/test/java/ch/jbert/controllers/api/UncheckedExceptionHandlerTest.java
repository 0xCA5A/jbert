package ch.jbert.controllers.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;

import ch.jbert.models.Error;
import ch.jbert.utils.UncheckedException;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.test.annotation.MicronautTest;

@MicronautTest
public class UncheckedExceptionHandlerTest {

    @Inject
    private UncheckedExceptionHandler uncheckedExceptionHandler;
    
    @Test
    void handle_when_wrappedIllegalArgumentException_then_throwIllegalArgumentException() {
        final UncheckedException e = UncheckedException.wrapThrowable(new IllegalArgumentException());
        assertThrows(IllegalArgumentException.class, () -> uncheckedExceptionHandler.handle(HttpRequest.GET("/"), e));
    }

    @Test
    void handle_when_wrappedGenericException_then_serverError() {
        final UncheckedException e = UncheckedException.wrapThrowable(new Exception("foo"));
        final HttpResponse response = uncheckedExceptionHandler.handle(HttpRequest.GET("/"), e);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.getCode(), response.code());
        assertEquals(new Error(HttpStatus.INTERNAL_SERVER_ERROR.getCode(), "foo"), response.body());
    }
}