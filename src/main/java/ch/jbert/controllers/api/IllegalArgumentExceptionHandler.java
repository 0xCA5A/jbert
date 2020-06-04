package ch.jbert.controllers.api;

import javax.inject.Singleton;

import ch.jbert.models.Error;
import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.ExceptionHandler;

@Produces
@Singleton
@Requires(classes = { IllegalArgumentException.class, ExceptionHandler.class })
public class IllegalArgumentExceptionHandler implements ExceptionHandler<IllegalArgumentException, HttpResponse> {

    @Override
    public HttpResponse handle(HttpRequest request, IllegalArgumentException exception) {
        return HttpResponse.badRequest(new Error(HttpStatus.BAD_REQUEST.getCode(), exception.getMessage()));
    }

}