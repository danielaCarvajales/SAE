package com.siscem.portal_sae.dtos.contact;

import java.time.LocalDate;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class ContactEditDTO {
	private Optional<String> numeroIdentificacion;
	private Optional<String> nombres;
	private Optional<String> apellidos;
	private Optional<String> pais;
	private Optional<String> departamento;
	private Optional<String> ciudad;
	private Optional<String> direccion;
	private Optional<String> codigoPais;
	private Optional<String> movil;
	private Optional<String> email;
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Optional<LocalDate> fechaNacimiento;
	private Optional<String> rutaImagen;

	// Foreign Keys
	private Optional<Integer> tipoIdentificacion;
	private Optional<Integer> cuenta;
}
