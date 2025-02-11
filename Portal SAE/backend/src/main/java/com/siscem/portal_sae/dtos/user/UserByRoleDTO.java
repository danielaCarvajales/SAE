package com.siscem.portal_sae.dtos.user;

import lombok.Data;

@Data
public class UserByRoleDTO {
	private Integer codigo;
	private String nombre;
	private String email;
	private String codigoPais;
	private String numeroMovil;
}
