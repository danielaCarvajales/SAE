package com.siscem.portal_sae.dtos.task;

import java.time.LocalDateTime;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class TaskEditDTO {
	private Optional<String> asunto;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Optional<LocalDateTime> fechaHoraInicio;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Optional<LocalDateTime> fechaHoraFin;
	private Optional<String> porcentajeProgreso;
	private Optional<String> descripcion;
	private Optional<String> observacion;

	// Foreign Keys
	private Optional<Integer> consultorAsignado;
	private Optional<Integer> empresarioAsignado;
	private Optional<Integer> cuentaAsignada;
	private Optional<Integer> estado;
	private Optional<Integer> tipo;
}
