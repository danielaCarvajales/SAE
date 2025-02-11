package com.siscem.portal_sae.dtos.incident;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class IncidentDTO {
	private Integer codigo;
	private String pregunta;
	private String respuesta;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime fechaHoraInicio;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime fechaHoraFin;

	// Foreign Keys
	private String codigoConsultor;
	private String codigoEmpresario;
	private String categoria;
	private String estado;
}
