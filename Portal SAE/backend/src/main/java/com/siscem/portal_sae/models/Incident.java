package com.siscem.portal_sae.models;

import java.time.LocalDateTime;

import com.siscem.portal_sae.models.relationships.IncidentCategories;
import com.siscem.portal_sae.models.relationships.IncidentStatus;

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
@Table(name = "INCIDENCIA")
public class Incident {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer codigo;

	@Column(name = "pregunta", nullable = true, length = 2000)
	private String pregunta;

	@Column(name = "respuesta", nullable = true)
	private String respuesta;

	@Column(name = "fecha_hora_inicio", nullable = true)
	private LocalDateTime fechaHoraInicio;

	@Column(name = "fecha_hora_fin", nullable = true)
	private LocalDateTime fechaHoraFin;

	// Foreign Keys

	@ManyToOne
	@JoinColumn(name = "codigo_consultor", nullable = false)
	private User codigoConsultor;

	@ManyToOne
	@JoinColumn(name = "codigo_empresario", nullable = false)
	private User codigoEmpresario;

	@ManyToOne
	@JoinColumn(name = "categoria", nullable = false)
	private IncidentCategories categoria;

	@ManyToOne
	@JoinColumn(name = "estado", nullable = false)
	private IncidentStatus estado;
}
