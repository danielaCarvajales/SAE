package com.siscem.portal_sae.dtos.contact;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class ContactCreateDTO {
	private String numeroIdentificacion;
	private String nombres;
	private String apellidos;
	private String pais;
	private String departamento;
	private String ciudad;
	private String direccion;
	private String codigoPais;
	private String movil;
	private String email;
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate fechaNacimiento;
	private String rutaImagen;

	// Foreign Keys
	private Integer tipoIdentificacion;
	private Integer cuenta;
}
