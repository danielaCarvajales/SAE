package com.siscem.portal_sae.controllers;

import java.util.List;

import com.siscem.portal_sae.dtos.email.EmailSendDTO;
import com.siscem.portal_sae.models.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.google.api.services.gmail.model.Message;

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

	@PostMapping("/send")
	public ResponseEntity<String> sendEmail(
			@RequestHeader("Authorization") String authorizationHeader,
			@RequestBody EmailSendDTO emailDTO) {
		if (jwtService.validateToken(authorizationHeader)) {
			return emailService.sendEmail(emailDTO.getTo(), emailDTO.getSubject(), emailDTO.getBody(), emailDTO.getUserCode());
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}


	// Endpoint para obtener correos de un usuario (sin DTO)
	@GetMapping("/user/{userCode}")
	@Operation(summary = "Retrieves all emails from a specified user (raw)")
	public ResponseEntity<List<Email>> getEmails(
			@RequestHeader("Authorization") String authorizationHeader,
			@PathVariable Integer userCode) {
		if (jwtService.validateToken(authorizationHeader)) {
			return emailService.getEmails(userCode);
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}

	@GetMapping("/inbox")
	public ResponseEntity<List<Message>> getInboxEmails(
			@RequestHeader("Authorization") String authorizationHeader) {
		if (jwtService.validateToken(authorizationHeader)) {
			return ResponseEntity.ok(emailService.getInboxEmails());
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}





}
