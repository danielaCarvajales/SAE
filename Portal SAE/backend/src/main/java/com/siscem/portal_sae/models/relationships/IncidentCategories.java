package com.siscem.portal_sae.models.relationships;

import java.util.List;

import com.siscem.portal_sae.models.Incident;

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
@Table(name = "CATEGORIAS_INCIDENCIA")
public class IncidentCategories {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer codigo;

	@Column(name = "nombre_categoria", nullable = false, length = 100)
	private String nombreCategoria;

	// Foreign Keys

	@OneToMany(mappedBy = "categoria")
	private List<Incident> incidencias;

}
