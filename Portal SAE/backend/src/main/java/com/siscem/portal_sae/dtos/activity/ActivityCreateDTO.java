package com.siscem.portal_sae.dtos.activity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class ActivityCreateDTO {
	private String porcentajeProgreso;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime fechaHoraInicio;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime fechaHoraFin;
	private String descripcion;
	private Integer horasTrabajo;

	// Foreign Keys
	private Integer codigoTarea;
	private Integer contactoAsignado;
	private Integer estado;
	private Integer unidadTrabajo;
}
