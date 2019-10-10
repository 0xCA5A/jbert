package utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.typesafe.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.Environment;
import play.api.OptionalSourceMapper;
import play.api.UsefulException;
import play.api.routing.Router;
import play.http.DefaultHttpErrorHandler;
import play.libs.Json;
import play.mvc.Http.RequestHeader;
import play.mvc.Result;
import play.mvc.Results;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;


/**
 * Error Handler ensuring that errors in JSON endpoints are reported using a JSON result.
 */
@Singleton
public class GlobalHttpErrorHandler extends DefaultHttpErrorHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalHttpErrorHandler.class);
    private static final List<String> JSON_ENDPOINTS = Collections.singletonList("/api");

    @Inject
    public GlobalHttpErrorHandler(Config config, Environment environment, OptionalSourceMapper sourceMapper, Provider<Router> routes) {
        super(config, environment, sourceMapper, routes);
    }

    @Override
    protected CompletionStage<Result> onProdServerError(RequestHeader request, UsefulException exception) {
        return handleServerError(request, exception, exception.getClass().getName());
    }

    @Override
    protected CompletionStage<Result> onDevServerError(RequestHeader request, UsefulException exception) {
        return handleServerError(request, exception, exception.getMessage());
    }

    @Override
    public CompletionStage<Result> onClientError(RequestHeader request, int statusCode, String message) {
        logger.info("Client error, status code {}, for ({}) [{}] message: {}", statusCode, request.method(), request.uri(), message);
        return isJsonEndpoint(request)
                ? CompletableFuture.completedFuture(Results.status(statusCode, toJsonMessage(message)))
                : super.onClientError(request, statusCode, message);
    }

    private CompletionStage<Result> handleServerError(RequestHeader request, UsefulException exception, String message) {
        logger.info("Server error: {}, for ({}) [{}] message: {}", exception.getMessage(), request.method(), request.uri(), message);
        return isJsonEndpoint(request)
                ? CompletableFuture.completedFuture(Results.internalServerError(toJsonMessage(message)))
                : super.onProdServerError(request, exception);
    }

    private boolean isJsonEndpoint(RequestHeader request) {
        final String path = request.path();
        return JSON_ENDPOINTS.stream()
                .anyMatch(path::startsWith);
    }

    private JsonNode toJsonMessage(String message) {
        return Json.toJson(message);
    }
}
