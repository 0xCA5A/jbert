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
@Requires(classes = { Exception.class, ExceptionHandler.class })
public class GenericExceptionHandler implements ExceptionHandler<Exception, HttpResponse> {

    @Override
    public HttpResponse handle(HttpRequest request, Exception exception) {
        return HttpResponse.serverError(new Error(HttpStatus.BAD_REQUEST.getCode(), exception.getMessage()));
    }

}