package com.siscem.portal_sae.models;

import java.time.LocalDateTime;

import com.siscem.portal_sae.models.relationships.ActivityStatus;
import com.siscem.portal_sae.models.relationships.WorkUnitsActivity;

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
@Table(name = "ACTIVIDAD")
public class Activity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer codigo;

	@Column(name = "porcentaje_progreso", nullable = true, length = 4)
	private String porcentajeProgreso;

	@Column(name = "fecha_hora_inicio", nullable = true)
	private LocalDateTime fechaHoraInicio;

	@Column(name = "fecha_hora_fin", nullable = true)
	private LocalDateTime fechaHoraFin;

	@Column(name = "descripcion", nullable = true, length = 2000)
	private String descripcion;

	@Column(name = "horas_trabajo", nullable = true)
	private Integer horasTrabajo;

	// Foreign Keys

	@ManyToOne
	@JoinColumn(name = "codigo_tarea", nullable = false)
	private Task codigoTarea;

	@ManyToOne
	@JoinColumn(name = "contacto_asignado", nullable = false)
	private Contact contactoAsignado;

	@ManyToOne
	@JoinColumn(name = "estado", nullable = false)
	private ActivityStatus estado;

	@ManyToOne
	@JoinColumn(name = "unidad_trabajo", nullable = false)
	private WorkUnitsActivity unidadTrabajo;

}
