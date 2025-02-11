package com.siscem.portal_sae.models;

import java.util.List;

import com.siscem.portal_sae.models.relationships.UserRoles;

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
@Table(name = "USUARIO")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer codigo;

	@Column(name = "nombre", nullable = false, length = 100)
	private String nombre;

	@Column(name = "contrasena", nullable = false, length = 100)
	private String contrasena;

	@Column(name = "email", nullable = false, length = 100)
	private String email;

	@Column(name = "codigo_pais", nullable = false, length = 4)
	private String codigoPais;

	@Column(name = "numero_movil", nullable = false, length = 15)
	private String numeroMovil;

	// Foreign Keys

	@ManyToOne
	@JoinColumn(name = "rol", nullable = false)
	private UserRoles rol;

	@OneToMany(mappedBy = "preguntaRealizadaPor", cascade = CascadeType.REMOVE)
	private List<Faq> preguntasRealizadas;

	@OneToMany(mappedBy = "respuestaRealizadaPor", cascade = CascadeType.REMOVE)
	private List<Faq> respuestasRealizadas;

	@OneToMany(mappedBy = "codigoConsultor", cascade = CascadeType.REMOVE)
	private List<Event> consultorDeEvento;

	@OneToMany(mappedBy = "codigoEmpresario", cascade = CascadeType.REMOVE)
	private List<Event> empresarioDeEvento;

	@OneToMany(mappedBy = "codigoUsuario", cascade = CascadeType.REMOVE)
	private List<Account> cuentasAsignadasUsuario;

	@OneToMany(mappedBy = "codigoConsultorAsignado", cascade = CascadeType.REMOVE)
	private List<Account> cuentasAsignadasConsultor;

	@OneToMany(mappedBy = "consultorAsignado", cascade = CascadeType.REMOVE)
	private List<Task> tareasAsignadasConsultor;

	@OneToMany(mappedBy = "empresarioAsignado", cascade = CascadeType.REMOVE)
	private List<Task> tareasAsignadasEmpresario;

	@OneToMany(mappedBy = "codigoConsultor", cascade = CascadeType.REMOVE)
	private List<Incident> incidenciasAtendidasPorConsultor;

	@OneToMany(mappedBy = "codigoEmpresario", cascade = CascadeType.REMOVE)
	private List<Incident> incidenciasReportadasPorEmpresario;
	
	@OneToMany(mappedBy = "usuarioAsignado", cascade = CascadeType.REMOVE)
	private List<Email> emails;

}
