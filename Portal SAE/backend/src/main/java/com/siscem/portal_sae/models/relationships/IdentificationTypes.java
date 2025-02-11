package com.siscem.portal_sae.models.relationships;

import java.util.List;

import com.siscem.portal_sae.models.Contact;
import com.siscem.portal_sae.models.Account;

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
@Table(name = "TIPOS_IDENTIFICACION")
public class IdentificationTypes {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer codigo;

	@Column(name = "nombre_tipo", nullable = false, length = 100)
	private String nombreTipo;

	// Foreign Keys

	@OneToMany(mappedBy = "tipoIdentificacion")
	private List<Account> cuentas;

	@OneToMany(mappedBy = "tipoIdentificacion")
	private List<Contact> contactos;
}
