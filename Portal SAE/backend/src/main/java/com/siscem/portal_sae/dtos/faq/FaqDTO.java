package com.siscem.portal_sae.dtos.faq;

import lombok.Data;

@Data
public class FaqDTO {
	private Integer codigo;
	private String pregunta;
	private String respuesta;
	private boolean aprobadoSuperadmin;

	// Foreign Keys
	private String categoria;
	private String preguntaRealizadaPor;
	private String respuestaRealizadaPor;
}
