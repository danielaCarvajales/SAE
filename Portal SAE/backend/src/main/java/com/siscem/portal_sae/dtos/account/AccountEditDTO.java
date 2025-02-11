package com.siscem.portal_sae.dtos.account;

import java.util.Optional;

import lombok.Data;

@Data
public class AccountEditDTO {
	private Optional<String> numeroIdentificacion;
	private Optional<String> email;
	private Optional<String> paginaWeb;
	private Optional<String> codigoPais;
	private Optional<String> telefonoFijo;
	private Optional<String> telefonoSecundario;
	private Optional<String> sectorEconomico;
	private Optional<String> pais;
	private Optional<String> departamento;
	private Optional<String> ciudad;
	private Optional<Integer> numeroEmpleados;

	// Foreign Keys
	private Optional<Integer> tipoIdentificacion;
	private Optional<Integer> tipoServicio;
	private Optional<Integer> codigoUsuario;
	private Optional<Integer> codigoConsultorAsignado;
}
