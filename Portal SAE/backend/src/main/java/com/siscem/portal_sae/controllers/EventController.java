package com.siscem.portal_sae.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.siscem.portal_sae.dtos.event.EventCreateDTO;
import com.siscem.portal_sae.dtos.event.EventDTO;
import com.siscem.portal_sae.dtos.event.EventEditDTO;
import com.siscem.portal_sae.services.EventService;
import com.siscem.portal_sae.services.JwtService;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/evento")
public class EventController {

	@Autowired
	EventService eventService;

	@Autowired
	private JwtService jwtService;

	@Operation(summary = "Returns a list of events based on the user's code and role.")
	@GetMapping("/usuario/{userCode}/{role}")
	public ResponseEntity<List<EventDTO>> getEventsForUser(@RequestHeader("Authorization") String authorizationHeader,
			@PathVariable Integer userCode, @PathVariable String role) {
		if (jwtService.validateToken(authorizationHeader)) {
			return eventService.getEventsForUser(userCode, role);
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}

	@Operation(summary = "Creates an event within the database.")
	@PostMapping
	public ResponseEntity<String> createEvent(@RequestHeader("Authorization") String authorizationHeader,
			@RequestBody EventCreateDTO event) {
		if (jwtService.validateToken(authorizationHeader)) {
			return eventService.createEvent(event);
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}

	@Operation(summary = "Updates an event within the database.")
	@PutMapping("/{eventCode}")
	public ResponseEntity<String> editEvent(@RequestHeader("Authorization") String authorizationHeader,
			@PathVariable Integer eventCode, @RequestBody EventEditDTO event) {
		if (jwtService.validateToken(authorizationHeader)) {
			return eventService.editEvent(eventCode, event);
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}

	@Operation(summary = "Deletes an event within the database.")
	@DeleteMapping("/{eventCode}")
	public ResponseEntity<String> deleteEvent(@RequestHeader("Authorization") String authorizationHeader,
			@PathVariable Integer eventCode) {
		if (jwtService.validateToken(authorizationHeader)) {
			return eventService.deleteEvent(eventCode);
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}
}
