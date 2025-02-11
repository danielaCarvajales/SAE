package com.siscem.portal_sae.dtos.event;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class EventDTO {
	private Integer codigo;
	private String asunto;
	private String porcentajeProgreso;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime fechaHoraInicio;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime fechaHoraFin;
	private Float tiempoDuracion;
	private String descripcion;

	// Foreign Keys
	private String codigoConsultor;
	private String codigoEmpresario;
	private String estado;
	private String tipo;
	private String visibilidad;
	private String notificarPor;
	private String cuentaAsignada;
	private String contactoAsignado;
}
