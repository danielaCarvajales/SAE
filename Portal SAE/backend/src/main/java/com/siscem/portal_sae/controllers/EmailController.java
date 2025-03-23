package com.siscem.portal_sae.controllers;

import com.siscem.portal_sae.dtos.email.LoginRequestDTO;
import com.siscem.portal_sae.services.EmailService;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/email")

public class EmailController {

	private final EmailService emailService;

	public EmailController(EmailService emailService) {
		this.emailService = emailService;
	}

	@PostMapping(value = "/send", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<String> sendEmail(
			@RequestPart("to") String to,
			@RequestPart("subject") String subject,
			@RequestPart("body") String body,
			@RequestPart("email") String userEmail,
			@RequestPart("password") String userPassword,
			@RequestPart(value = "attachments", required = false) List<MultipartFile> attachments) {

		boolean emailSent = emailService.sendEmail(to, subject, body, userEmail, userPassword, attachments);

		if (emailSent) {
			return ResponseEntity.ok("Correo enviado correctamente");
		} else {
			return ResponseEntity.status(500).body("Error al enviar el correo");
		}
	}


	@PostMapping("/fetch")
	public ResponseEntity<List<String>> fetchEmails(@RequestBody LoginRequestDTO request) {
		String userEmail = request.getEmail();
		String userPassword = request.getPassword();
		return ResponseEntity.ok(emailService.fetchEmails(userEmail, userPassword));
	}

	@RequestMapping(value = "/fetch", method = RequestMethod.OPTIONS)
	public ResponseEntity<?> handleOptions() {
		return ResponseEntity.ok().build();
	}



}
