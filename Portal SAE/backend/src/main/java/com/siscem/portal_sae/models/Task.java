package com.siscem.portal_sae.models;

import java.time.LocalDateTime;
import java.util.List;

import com.siscem.portal_sae.models.relationships.TaskStatus;
import com.siscem.portal_sae.models.relationships.TaskTypes;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "TAREA")
public class Task {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer codigo;

	@Column(name = "asunto", nullable = true, length = 255)
	private String asunto;

	@Column(name = "fecha_hora_inicio", nullable = true)
	private LocalDateTime fechaHoraInicio;

	@Column(name = "fecha_hora_fin", nullable = true)
	private LocalDateTime fechaHoraFin;

	@Column(name = "porcentaje_progreso", nullable = true, length = 4)
	private String porcentajeProgreso;

	@Column(name = "descripcion", nullable = true, length = 2000)
	private String descripcion;

	@Column(name = "observacion", nullable = true)
	private String observacion;

	// Foreign Keys

	@ManyToOne
	@JoinColumn(name = "codigo_consultor_asignado", nullable = false)
	private User consultorAsignado;

	@ManyToOne
	@JoinColumn(name = "codigo_empresario_asignado", nullable = false)
	private User empresarioAsignado;

	@ManyToOne
	@JoinColumn(name = "codigo_cuenta_asignada", nullable = false)
	private Account cuentaAsignada;

	@ManyToOne
	@JoinColumn(name = "estado", nullable = false)
	private TaskStatus estado;

	@ManyToOne
	@JoinColumn(name = "tipo", nullable = false)
	private TaskTypes tipo;

	@OneToMany(mappedBy = "codigoTarea", cascade = CascadeType.REMOVE)
	private List<Activity> actividades;
}
