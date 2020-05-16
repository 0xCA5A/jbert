package ch.jbert;

import io.micronaut.runtime.Micronaut;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.tags.Tag;

@OpenAPIDefinition(
    info = @Info(
        title = "${api.title}",
        version = "${api.version}",
        description = "${api.description}",
        license = @License(name = "${api.license.name}")
    ),
    tags = {
        @Tag(name = "${api.tags.system.name}", description = "${api.tags.system.description}"),
        @Tag(name = "${api.tags.playlists.name}", description = "${api.tags.playlists.description}"),
        @Tag(name = "${api.tags.tracks.name}", description = "${api.tags.tracks.description}")
    }
)
public class Application {
    public static void main(String[] args) {
        Micronaut.run(Application.class);
    }
}