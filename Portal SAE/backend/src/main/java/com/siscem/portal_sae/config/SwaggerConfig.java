package com.siscem.portal_sae.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;


@Configuration
public class SwaggerConfig {
	
	/**
	 * Configures and provides an OpenAPI instance for Swagger documentation.
	 * 
	 * This method creates an OpenAPI instance and sets up a security scheme named "api-key".
	 * The security scheme specifies that API key authentication is used and the API key
	 * is expected to be provided in the HTTP header.
	 * 
	 * @return An OpenAPI instance configured with security scheme and requirements.
	 */
	@Bean
	OpenAPI customOpenApi() {
		return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("api-key", new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                                .name("Authorization")))
                .addSecurityItem(new SecurityRequirement().addList("api-key"));
	}
	
}
