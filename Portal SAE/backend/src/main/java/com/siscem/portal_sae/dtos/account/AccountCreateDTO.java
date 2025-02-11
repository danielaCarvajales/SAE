package com.siscem.portal_sae.dtos.account;

import lombok.Data;

@Data
public class AccountCreateDTO {
	private String numeroIdentificacion;
	private String email;
	private String paginaWeb;
	private String codigoPais;
	private String telefonoFijo;
	private String telefonoSecundario;
	private String sectorEconomico;
	private String pais;
	private String departamento;
	private String ciudad;
	private Integer numeroEmpleados;

	// Foreign Keys
	private Integer tipoIdentificacion;
	private Integer tipoServicio;
	private Integer codigoUsuario;
	private Integer codigoConsultorAsignado;
}
