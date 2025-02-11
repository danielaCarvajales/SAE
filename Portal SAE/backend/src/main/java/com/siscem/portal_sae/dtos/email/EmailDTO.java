package com.siscem.portal_sae.dtos.email;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class EmailDTO {
	private String codigo;
	private String remitente;
	private String destinatario;
	private String asunto;
	private String contenido;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date fechaEnviado;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date fechaRecibido;
	private String eventoAsignado;
}
