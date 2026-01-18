package com.secretsanta.api.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI 3.0 configuration for API documentation
 * Access interactive Swagger UI at: http://localhost:8080/swagger-ui/index.html
 * Access OpenAPI 3.0 specification (JSON) at: http://localhost:8080/v3/api-docs
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";
        
        return new OpenAPI()
                .info(new Info()
                        .title("Secret Santa API")
                        .version("1.0.0")
                        .description("REST API for Secret Santa. " +
                                "This API provides endpoints for managing gift ideas, recipients, " +
                                "email communication, and authentication.")
                        .contact(new Contact()
                                .name("Secret Santa Team")
                                .email("support@secretsanta.com"))
                        .license(new License()
                                .name("Private")
                                .url("http://secretsanta.com")))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .name(securitySchemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT authentication token. Obtain from /api/auth/login endpoint.")));
    }
}
