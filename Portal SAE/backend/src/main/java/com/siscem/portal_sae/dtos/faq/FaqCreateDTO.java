package com.siscem.portal_sae.dtos.faq;

import lombok.Data;

@Data
public class FaqCreateDTO {
	private String pregunta;
	private String respuesta;
	private boolean aprobadoSuperadmin;

	// Foreign Keys
	private Integer categoria;
	private Integer preguntaRealizadaPor;
	private Integer respuestaRealizadaPor;
}
