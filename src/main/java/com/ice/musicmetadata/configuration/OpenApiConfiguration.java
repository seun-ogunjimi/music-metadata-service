package com.ice.musicmetadata.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(
        name = {"springdoc.api-docs.enabled"},
        matchIfMissing = true
)
@ConditionalOnWebApplication
public class OpenApiConfiguration {
    /**
     * OpenAPI configuration bean.
     *
     * @return OpenAPI object with API information
     */
    @Bean
    public OpenAPI api() {
        return new OpenAPI()
                .info(new io.swagger.v3.oas.models.info.Info()
                        .title("Music Metadata API")
                        .version("1.0.0")
                        .description("API for managing music metadata"));
    }
}
