package com.siscem.portal_sae.dtos.faq;

import java.util.Optional;

import lombok.Data;

@Data
public class FaqEditDTO {
	private Optional<String> pregunta;
	private Optional<String> respuesta;

	// Foreign Keys
	private Optional<Integer> categoria;
	private Optional<Integer> preguntaRealizadaPor;
	private Optional<Integer> respuestaRealizadaPor;
}
