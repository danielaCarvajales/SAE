package com.siscem.portal_sae.dtos.activity;

import java.time.LocalDateTime;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class ActivityEditDTO {
	private Optional<String> porcentajeProgreso;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Optional<LocalDateTime> fechaHoraInicio;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Optional<LocalDateTime> fechaHoraFin;
	private Optional<String> descripcion;
	private Optional<Integer> horasTrabajo;

	// Foreign Keys
	private Optional<Integer> codigoTarea;
	private Optional<Integer> contactoAsignado;
	private Optional<Integer> estado;
	private Optional<Integer> unidadTrabajo;
}
