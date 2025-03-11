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

	@PostMapping("/send")
	@Operation(summary = "Sends an email using Gmail API")
	public ResponseEntity<String> sendEmail(
			@RequestHeader("Authorization") String authorizationHeader,
			@RequestParam String to,
			@RequestParam String subject,
			@RequestParam String body,
			@RequestParam Integer userCode) {
		if (jwtService.validateToken(authorizationHeader)) {
			return emailService.sendEmail(to, subject, body, userCode);
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

	@GetMapping("/test")
	public ResponseEntity<String> testEmail() {
		return ResponseEntity.ok("API de Mail está funcionando en el puerto 8080");
	}

}
