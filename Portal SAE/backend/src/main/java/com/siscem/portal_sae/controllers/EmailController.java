package com.siscem.portal_sae.controllers;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.siscem.portal_sae.models.Email;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.siscem.portal_sae.dtos.email.EmailDTO;
import com.siscem.portal_sae.dtos.email.EmailDetailsDTO;
import com.siscem.portal_sae.services.EmailService;
import com.siscem.portal_sae.services.JwtService;

import io.swagger.v3.oas.annotations.Operation;

@Controller
@RequestMapping("/email")
public class EmailController {

	@Autowired
	EmailService emailService;

	@Autowired
	private JwtService jwtService;

	@PostMapping("/capturar")
	@Operation(summary = "Retrieves and saves all emails from user")
	public ResponseEntity<String> retrieveAndSaveEmails(@RequestHeader(value = "Authorization", required = false) String authorizationHeader,
														@RequestBody EmailDetailsDTO email) {
		if (authorizationHeader == null || authorizationHeader.isEmpty()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body("Authorization header is missing");
		}

		if (jwtService.validateToken(authorizationHeader)) {
			return emailService.retrieveAndSaveEmails(email.getAnfitrion(), email.getCorreo(), email.getContrasena(),
					email.getProtocolo(), email.getPuerto(), email.getCodigoUsuario());
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}


	@GetMapping("/listar/usuario/{userCode}")
	@Operation(summary = "Retrieves all emails from a specified user")
	public ResponseEntity<List<EmailDTO>> getAllUserEmails(@RequestHeader("Authorization") String authorizationHeader,
			@PathVariable Integer userCode) {
		if (jwtService.validateToken(authorizationHeader)) {
			return emailService.getAllUserEmails(userCode);
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}


	@GetMapping("/listar/evento/{eventCode}")
	@Operation(summary = "Retrieves all emails from a specified event")
	public ResponseEntity<List<EmailDTO>> getAllEventEmails(@RequestHeader("Authorization") String authorizationHeader,
			@PathVariable Integer eventCode) {
		if (jwtService.validateToken(authorizationHeader)) {
			return emailService.getAllEventEmails(eventCode);
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}



	@PutMapping("/evento/{eventCode}")
	@Operation(summary = "Updates the assigned events of an email")
	public ResponseEntity<String> updateAssignedEvents(@RequestHeader("Authorization") String authorizationHeader,
			@PathVariable Integer eventCode, @RequestBody List<String> email) {
		if (jwtService.validateToken(authorizationHeader)) {
			return emailService.updateAssignedEvents(eventCode, email);
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}





}
