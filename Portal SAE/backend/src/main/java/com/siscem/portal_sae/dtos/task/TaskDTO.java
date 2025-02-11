package com.siscem.portal_sae.dtos.task;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class TaskDTO {
	private Integer codigo;
	private String asunto;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime fechaHoraInicio;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime fechaHoraFin;
	private String porcentajeProgreso;
	private String descripcion;
	private String observacion;

	// Foreign Keys
	private String consultorAsignado;
	private String empresarioAsignado;
	private String cuentaAsignada;
	private String estado;
	private String tipo;
}
