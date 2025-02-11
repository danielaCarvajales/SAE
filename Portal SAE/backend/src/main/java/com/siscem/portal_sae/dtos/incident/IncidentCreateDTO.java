package com.siscem.portal_sae.dtos.incident;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class IncidentCreateDTO {
	private String pregunta;
	private String respuesta;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime fechaHoraInicio;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime fechaHoraFin;

	// Foreign Keys
	private Integer codigoConsultor;
	private Integer codigoEmpresario;
	private Integer categoria;
	private Integer estado;
}
