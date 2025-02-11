package com.siscem.portal_sae.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class GlobalCorsConfiguration implements WebMvcConfigurer {

	/**
	 * Retrieves the allowed origins from the application properties for CORS
	 * configuration.
	 * 
	 * This annotation injects the values of the "cors.allowed.origins" property
	 * from the application.properties. The allowed origins specify the origins that
	 * are permitted to make cross-origin requests to the application.
	 */
	@Value("${cors.allowed.origins}")
	private String[] allowedOrigins;

	/**
	 * Configures global CORS (Cross-Origin Resource Sharing) settings for the
	 * application.
	 * 
	 * This class implements the WebMvcConfigurer interface to provide custom CORS
	 * configuration.
	 * 
	 * @param registry CorsRegistry object used to configure CORS mappings.
	 */
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**").allowedOrigins(allowedOrigins).allowedMethods("GET", "POST", "PUT", "DELETE")
				.allowedHeaders("*");
	}
}
