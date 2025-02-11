package com.siscem.portal_sae.dtos.task;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class TaskCreateDTO {
	private String asunto;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime fechaHoraInicio;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime fechaHoraFin;
	private String porcentajeProgreso;
	private String descripcion;
	private String observacion;

	// Foreign Keys
	private Integer consultorAsignado;
	private Integer empresarioAsignado;
	private Integer cuentaAsignada;
	private Integer estado;
	private Integer tipo;
}
