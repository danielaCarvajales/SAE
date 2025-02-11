package com.siscem.portal_sae.models;

import java.util.List;

import com.siscem.portal_sae.models.relationships.IdentificationTypes;
import com.siscem.portal_sae.models.relationships.AccountServiceTypes;

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
@Table(name = "CUENTA")
public class Account {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer codigo;

	@Column(name = "numero_identificacion", nullable = true, length = 30)
	private String numeroIdentificacion;

	@Column(name = "email", nullable = true, length = 100)
	private String email;

	@Column(name = "pagina_web", nullable = true, length = 100)
	private String paginaWeb;

	@Column(name = "codigo_pais", nullable = false, length = 4)
	private String codigoPais;

	@Column(name = "telefono_fijo", nullable = true, length = 15)
	private String telefonoFijo;

	@Column(name = "telefono_secundario", nullable = true, length = 15)
	private String telefonoSecundario;

	@Column(name = "sector_economico", nullable = true, length = 150)
	private String sectorEconomico;

	@Column(name = "pais", nullable = true, length = 100)
	private String pais;

	@Column(name = "departamento", nullable = true, length = 100)
	private String departamento;

	@Column(name = "ciudad", nullable = true, length = 100)
	private String ciudad;

	@Column(name = "numero_empleados", nullable = true)
	private Integer numeroEmpleados;

	// Foreign Keys

	@ManyToOne
	@JoinColumn(name = "tipo_identificacion", nullable = false)
	private IdentificationTypes tipoIdentificacion;

	@ManyToOne
	@JoinColumn(name = "tipo_servicio", nullable = false)
	private AccountServiceTypes tipoServicio;

	@ManyToOne
	@JoinColumn(name = "codigo_usuario", nullable = false)
	private User codigoUsuario;

	@ManyToOne
	@JoinColumn(name = "codigo_consultor_asignado", nullable = false)
	private User codigoConsultorAsignado;

	@OneToMany(mappedBy = "cuentaAsignada", cascade = CascadeType.REMOVE)
	private List<Event> eventosAsignados;

	@OneToMany(mappedBy = "cuenta", cascade = CascadeType.REMOVE)
	private List<Contact> contactos;

	@OneToMany(mappedBy = "cuentaAsignada", cascade = CascadeType.REMOVE)
	private List<Task> tareasAsignadas;

}
