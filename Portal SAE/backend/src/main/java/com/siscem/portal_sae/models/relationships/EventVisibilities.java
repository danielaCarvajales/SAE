package com.siscem.portal_sae.models.relationships;

import java.util.List;

import com.siscem.portal_sae.models.Event;

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
@Table(name = "VISIBILIDADES_EVENTO")
public class EventVisibilities {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer codigo;

	@Column(name = "nombre_visibilidad", nullable = false, length = 100)
	private String nombreVisibilidad;

	// Foreign Keys

	@OneToMany(mappedBy = "visibilidad")
	private List<Event> eventos;
}
