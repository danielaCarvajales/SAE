package com.siscem.portal_sae.dtos.incident;

import java.time.LocalDateTime;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class IncidentEditDTO {
	private Optional<String> pregunta;
	private Optional<String> respuesta;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Optional<LocalDateTime> fechaHoraInicio;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Optional<LocalDateTime> fechaHoraFin;

	// Foreign Keys
	private Optional<Integer> codigoConsultor;
	private Optional<Integer> codigoEmpresario;
	private Optional<Integer> categoria;
	private Optional<Integer> estado;
}
