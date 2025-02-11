package com.siscem.portal_sae.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

	/**
	 * Configures and provides a ModelMapper instance.
	 * 
	 * This method creates and returns a ModelMapper instance, which is used for
	 * mapping between different Java objects. ModelMapper simplifies the process
	 * of mapping properties from one object to another, including complex mappings
	 * and nested objects.
	 * 
	 * @return A ModelMapper instance.
	 */
	@Bean
	ModelMapper modelMapper() {
		return new ModelMapper();
	}
}
