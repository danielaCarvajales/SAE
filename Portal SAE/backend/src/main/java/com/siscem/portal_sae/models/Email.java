package com.siscem.portal_sae.models;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "EMAIL")
public class Email {

	@Id
	private String codigo;

	@Column(name = "remitente", nullable = true, length = 10000)
	private String remitente;

	@Column(name = "destinatario", nullable = true, length = 10000)
	private String destinatario;

	@Column(name = "asunto", nullable = true, length = 10000)
	private String asunto;

	@Column(name = "contenido", nullable = true, length = 10000)
	private String contenido;

	@Column(name = "fecha_enviado", nullable = true)
	private Date fechaEnviado;

	@Column(name = "fecha_recibido", nullable = true)
	private Date fechaRecibido;

	@ManyToOne
	@JoinColumn(name = "usuario_asignado", nullable = false)
	private User usuarioAsignado;

	@ManyToOne
	@JoinColumn(name = "evento_asignado", nullable = true)
	private Event eventoAsignado;
}
