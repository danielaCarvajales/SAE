package com.siscem.portal_sae.dtos.event;

import java.time.LocalDateTime;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class EventEditDTO {
	private Optional<String> asunto;
	private Optional<String> porcentajeProgreso;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Optional<LocalDateTime> fechaHoraInicio;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Optional<LocalDateTime> fechaHoraFin;
	private Optional<Float> tiempoDuracion;
	private Optional<String> descripcion;

	// Foreign Keys
	private Optional<Integer> codigoConsultor;
	private Optional<Integer> codigoEmpresario;
	private Optional<Integer> estado;
	private Optional<Integer> tipo;
	private Optional<Integer> visibilidad;
	private Optional<Integer> notificarPor;
	private Optional<Integer> cuentaAsignada;
	private Optional<Integer> contactoAsignado;
}
