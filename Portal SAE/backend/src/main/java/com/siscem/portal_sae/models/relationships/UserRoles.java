package com.siscem.portal_sae.models.relationships;

import java.util.List;

import com.siscem.portal_sae.models.User;

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
@Table(name = "ROLES")
public class UserRoles {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer codigo;

	@Column(name = "nombre_rol", nullable = false, length = 100)
	private String nombreRol;

	// Foreign Keys

	@OneToMany(mappedBy = "rol")
	private List<User> usuarios;
}
