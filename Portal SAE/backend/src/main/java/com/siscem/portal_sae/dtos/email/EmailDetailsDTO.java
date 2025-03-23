package com.siscem.portal_sae.dtos.email;

import java.util.Optional;

import lombok.Data;

@Data
public class EmailDetailsDTO {
	private String correo;
	private String contrasena;
	private String protocolo;
	private String puerto;
	private Integer codigoUsuario;
	private Optional<Integer> codigoEvento;
}
