package com.siscem.portal_sae.models;

import com.siscem.portal_sae.models.relationships.FaqCategories;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "FAQ")
public class Faq {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer codigo;

	@Column(name = "pregunta", nullable = true, length = 2000)
	private String pregunta;

	@Column(name = "respuesta", nullable = true, length = 2000)
	private String respuesta;

	@Column(name = "aprobado_superadmin", nullable = true)
	private boolean aprobadoSuperadmin;

	// Foreign Keys

	@ManyToOne
	@JoinColumn(name = "categoria", nullable = false)
	private FaqCategories categoria;

	@ManyToOne
	@JoinColumn(name = "pregunta_realizada_por", nullable = false)
	private User preguntaRealizadaPor;

	@ManyToOne
	@JoinColumn(name = "respuesta_realizada_por", nullable = false)
	private User respuestaRealizadaPor;
}
