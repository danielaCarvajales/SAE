package com.siscem.portal_sae.dtos.user;

import lombok.Data;

@Data
public class UserLoginDTO {
	private Integer codigo;
	private String nombre;
	private String rol;
	private String email;
	private String codigoPais;
	private String numeroMovil;
}
