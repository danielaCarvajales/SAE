package com.siscem.portal_sae.models;

import java.time.LocalDateTime;
import java.util.List;

import com.siscem.portal_sae.models.relationships.EventNotificationMethods;
import com.siscem.portal_sae.models.relationships.EventStatus;
import com.siscem.portal_sae.models.relationships.EventTypes;
import com.siscem.portal_sae.models.relationships.EventVisibilities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PreRemove;
import jakarta.persistence.Table;
import lombok.Data;



@Data
@Entity
@Table(name = "EVENTO")
public class Event {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer codigo;

	@Column(name = "asunto", nullable = true, length = 255)
	private String asunto;

	@Column(name = "porcentaje_progreso", nullable = true, length = 4)
	private String porcentajeProgreso;

	@Column(name = "fecha_hora_inicio", nullable = true)
	private LocalDateTime fechaHoraInicio;

	@Column(name = "fecha_hora_fin", nullable = true)
	private LocalDateTime fechaHoraFin;

	@Column(name = "tiempo_duracion", nullable = true)
	private Float tiempoDuracion;

	@Column(name = "descripcion", nullable = true, length = 2000)
	private String descripcion;

	@PreRemove
    private void removeEmails() {
        if (emails != null) {
            for (Email email : emails) {
                email.setEventoAsignado(null);
            }
        }
    }
	
	// Foreign Keys

	@ManyToOne
	@JoinColumn(name = "codigo_consultor", nullable = false)
	private User codigoConsultor;

	@ManyToOne
	@JoinColumn(name = "codigo_empresario", nullable = false)
	private User codigoEmpresario;

	@ManyToOne
	@JoinColumn(name = "estado", nullable = false)
	private EventStatus estado;

	@ManyToOne
	@JoinColumn(name = "tipo", nullable = false)
	private EventTypes tipo;

	@ManyToOne
	@JoinColumn(name = "visibilidad", nullable = false)
	private EventVisibilities visibilidad;

	@ManyToOne
	@JoinColumn(name = "notificar_por", nullable = false)
	private EventNotificationMethods notificarPor;

	@ManyToOne
	@JoinColumn(name = "cuenta_asignada", nullable = false)
	private Account cuentaAsignada;

	@ManyToOne
	@JoinColumn(name = "contacto_asignado", nullable = false)
	private Contact contactoAsignado;

	@OneToMany(mappedBy = "eventoAsignado")
	private List<Email> emails;
}
