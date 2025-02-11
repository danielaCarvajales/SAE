package com.siscem.portal_sae.dtos.email;

import java.util.Optional;

import lombok.Data;

@Data
public class EmailDetailsDTO {
	private String anfitrion; // IMAP Server e.g. 'imap.example.com'
	private String correo; // EMAIL address e.g john.doe@example.com
	private String contrasena; // EMAIL password
	private String protocolo; // imap, pop3, smtp
	/*
	 * IMAP: 143, 993
	 * POP3: 110, 995
	 * SMTP: 25, 587, 465
	 */
	private String puerto;
	private Integer codigoUsuario;
	private Optional<Integer> codigoEvento;
}
