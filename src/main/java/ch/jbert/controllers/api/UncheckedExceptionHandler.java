package ch.jbert.controllers.api;

import javax.inject.Singleton;

import ch.jbert.models.Error;
import ch.jbert.utils.Throwables;
import ch.jbert.utils.UncheckedException;
import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.ExceptionHandler;

@Produces
@Singleton
@Requires(classes = { UncheckedException.class, ExceptionHandler.class })
public class UncheckedExceptionHandler implements ExceptionHandler<UncheckedException, HttpResponse> {

    @Override
    public HttpResponse handle(HttpRequest request, UncheckedException exception) {
        Throwables.propagateIfPossible(exception.getCause(), IllegalArgumentException.class);
        return HttpResponse.serverError(new Error(HttpStatus.INTERNAL_SERVER_ERROR.getCode(), exception.getCause().getMessage()));
    }

}