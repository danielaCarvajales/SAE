package com.siscem.portal_sae.models.relationships;

import java.util.List;

import com.siscem.portal_sae.models.Activity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "UNIDADES_TRABAJO_ACTIVIDAD")
public class WorkUnitsActivity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer codigo;

	@Column(name = "nombre_unidad", nullable = false, length = 100)
	private String nombreUnidad;

	// Foreign Keys

	@OneToMany(mappedBy = "unidadTrabajo")
	private List<Activity> actividades;

}
