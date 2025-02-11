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
@Table(name = "MEDIOS_NOTIFICACION_EVENTO")
public class EventNotificationMethods {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer codigo;

	@Column(name = "nombre_medio", nullable = false, length = 100)
	private String nombreMedio;

	// Foreign Keys

	@OneToMany(mappedBy = "notificarPor")
	private List<Event> eventos;

}
