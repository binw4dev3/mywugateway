package com.wu.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Exposes Swagger UI at /swagger-ui.html and OpenAPI JSON at /v3/api-docs.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI wuGatewayOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("WU Gateway API")
                        .description("""
                                REST API layer for the Western Union Gateway service. \
                                Exposes all DAS query operations, APN template retrieval, \
                                delivery service discovery, and raw gateway pass-through calls. \
                                All responses are wrapped in a consistent ApiResponse envelope \
                                with status, message, and data fields.""")
                        .version("1.0.0")
                        .contact(new Contact().name("WU Gateway Team")))
                .servers(List.of(
                        new Server().url("/").description("Local server")));
    }
}
