package com.siscem.portal_sae.dtos.event;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class EventCreateDTO {
	private String asunto;
	private String porcentajeProgreso;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime fechaHoraInicio;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime fechaHoraFin;
	private Float tiempoDuracion;
	private String descripcion;

	// Foreign Keys
	private Integer codigoConsultor;
	private Integer codigoEmpresario;
	private Integer estado;
	private Integer tipo;
	private Integer visibilidad;
	private Integer notificarPor;
	private Integer cuentaAsignada;
	private Integer contactoAsignado;
}
