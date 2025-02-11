package com.siscem.portal_sae.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.siscem.portal_sae.dtos.RelationshipsDTO;
import com.siscem.portal_sae.services.JwtService;
import com.siscem.portal_sae.services.RelationshipsService;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/relaciones")
public class RelationshipsController {

	@Autowired
	RelationshipsService relationshipsService;

	@Autowired
	private JwtService jwtService;

	@Operation(summary = "Obtiene una lista de las visibilidades de evento")
	@GetMapping("/visibilidades_evento")
	public ResponseEntity<List<RelationshipsDTO>> getEventVisibilities(
			@RequestHeader("Authorization") String authorizationHeader) {
		if (jwtService.validateToken(authorizationHeader)) {
			return relationshipsService.getEventVisibilities();
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}

	@Operation(summary = "Obtiene una lista de unidades de trabajo de actividad")
	@GetMapping("/unidades_trabajo_actividad")
	public ResponseEntity<List<RelationshipsDTO>> getWorkUnitsActivity(
			@RequestHeader("Authorization") String authorizationHeader) {
		if (jwtService.validateToken(authorizationHeader)) {
			return relationshipsService.getWorkUnitsActivity();
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}

	@Operation(summary = "Obtiene una lista de tipos de tarea")
	@GetMapping("/tipos_tarea")
	public ResponseEntity<List<RelationshipsDTO>> getTaskTypes(
			@RequestHeader("Authorization") String authorizationHeader) {
		if (jwtService.validateToken(authorizationHeader)) {
			return relationshipsService.getTaskTypes();
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}

	@Operation(summary = "Obtiene una lista de tipos de servicio de cuenta")
	@GetMapping("/tipos_servicio_cuenta")
	public ResponseEntity<List<RelationshipsDTO>> getAccountServiceTypes(
			@RequestHeader("Authorization") String authorizationHeader) {
		if (jwtService.validateToken(authorizationHeader)) {
			return relationshipsService.getAccountServiceTypes();
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}

	@Operation(summary = "Obtiene una lista de tipos de identificacion")
	@GetMapping("/tipos_identificacion")
	public ResponseEntity<List<RelationshipsDTO>> getIdentificationTypes(
			@RequestHeader("Authorization") String authorizationHeader) {
		if (jwtService.validateToken(authorizationHeader)) {
			return relationshipsService.getIdentificationTypes();
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}

	@Operation(summary = "Obtiene una lista de tipos de evento")
	@GetMapping("/tipos_evento")
	public ResponseEntity<List<RelationshipsDTO>> getEventTypes(
			@RequestHeader("Authorization") String authorizationHeader) {
		if (jwtService.validateToken(authorizationHeader)) {
			return relationshipsService.getEventTypes();
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}

	@Operation(summary = "Obtiene una lista de roles de usuario")
	@GetMapping("/roles_usuario")
	public ResponseEntity<List<RelationshipsDTO>> getUserRoles(
			@RequestHeader("Authorization") String authorizationHeader) {
		if (jwtService.validateToken(authorizationHeader)) {
			return relationshipsService.getUserRoles();
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}

	@Operation(summary = "Obtiene una lista de medios de notificacion de evento")
	@GetMapping("/medios_notificacion_evento")
	public ResponseEntity<List<RelationshipsDTO>> getEventNotificationMethods(
			@RequestHeader("Authorization") String authorizationHeader) {
		if (jwtService.validateToken(authorizationHeader)) {
			return relationshipsService.getEventNotificationMethods();
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}

	@Operation(summary = "Obtiene una lista de estados de tarea")
	@GetMapping("/estados_tarea")
	public ResponseEntity<List<RelationshipsDTO>> getTaskStatus(
			@RequestHeader("Authorization") String authorizationHeader) {
		if (jwtService.validateToken(authorizationHeader)) {
			return relationshipsService.getTaskStatus();
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}

	@Operation(summary = "Obtiene una lista de estados de incidencia")
	@GetMapping("/estados_incidencia")
	public ResponseEntity<List<RelationshipsDTO>> getIncidentStatus(
			@RequestHeader("Authorization") String authorizationHeader) {
		if (jwtService.validateToken(authorizationHeader)) {
			return relationshipsService.getIncidentStatus();
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}

	@Operation(summary = "Obtiene una lista de estados de evento")
	@GetMapping("/estados_evento")
	public ResponseEntity<List<RelationshipsDTO>> getEventStatus(
			@RequestHeader("Authorization") String authorizationHeader) {
		if (jwtService.validateToken(authorizationHeader)) {
			return relationshipsService.getEventStatus();
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}

	@Operation(summary = "Obtiene una lista de estados de actividad")
	@GetMapping("/estados_actividad")
	public ResponseEntity<List<RelationshipsDTO>> getActivityStatus(
			@RequestHeader("Authorization") String authorizationHeader) {
		if (jwtService.validateToken(authorizationHeader)) {
			return relationshipsService.getActivityStatus();
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}

	@Operation(summary = "Obtiene una lista de categorias de incidencia")
	@GetMapping("/categorias_incidencia")
	public ResponseEntity<List<RelationshipsDTO>> getIncidentCategories(
			@RequestHeader("Authorization") String authorizationHeader) {
		if (jwtService.validateToken(authorizationHeader)) {
			return relationshipsService.getIncidentCategories();
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}

	@Operation(summary = "Obtiene una lista de categorias de faq")
	@GetMapping("/categorias_faq")
	public ResponseEntity<List<RelationshipsDTO>> getFaqCategories(
			@RequestHeader("Authorization") String authorizationHeader) {
		if (jwtService.validateToken(authorizationHeader)) {
			return relationshipsService.getFaqCategories();
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}

}
