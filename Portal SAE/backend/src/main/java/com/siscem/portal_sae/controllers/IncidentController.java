package com.siscem.portal_sae.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.siscem.portal_sae.dtos.incident.IncidentCreateDTO;
import com.siscem.portal_sae.dtos.incident.IncidentDTO;
import com.siscem.portal_sae.dtos.incident.IncidentEditDTO;
import com.siscem.portal_sae.services.IncidentService;
import com.siscem.portal_sae.services.JwtService;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/incidencia")
public class IncidentController {

	@Autowired
	IncidentService incidentService;

	@Autowired
	private JwtService jwtService;

	@Operation(summary = "Returns a list of incidences given the user's code and role.")
	@GetMapping("/usuario/{userCode}/{role}")
	public ResponseEntity<List<IncidentDTO>> getIncidentsForUser(
			@RequestHeader("Authorization") String authorizationHeader, @PathVariable Integer userCode,
			@PathVariable String role) {
		if (jwtService.validateToken(authorizationHeader)) {
			return incidentService.getIncidentsForUser(userCode, role);
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}

	@Operation(summary = "Creates an incidence within the database.")
	@PostMapping
	public ResponseEntity<String> createIncident(@RequestHeader("Authorization") String authorizationHeader,
			@RequestBody IncidentCreateDTO incident) {
		if (jwtService.validateToken(authorizationHeader)) {
			return incidentService.createIncident(incident);
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}

	@Operation(summary = "Updates an incidence within the database.")
	@PutMapping("/{incidentCode}")
	public ResponseEntity<String> editIncident(@RequestHeader("Authorization") String authorizationHeader,
			@PathVariable Integer incidentCode, @RequestBody IncidentEditDTO incident) {
		if (jwtService.validateToken(authorizationHeader)) {
			return incidentService.editIncident(incidentCode, incident);
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}

}
