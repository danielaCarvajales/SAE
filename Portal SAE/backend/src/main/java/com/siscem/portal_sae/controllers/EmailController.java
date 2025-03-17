package com.siscem.portal_sae.controllers;

import com.siscem.portal_sae.dtos.email.EmailSendDTO;
import com.siscem.portal_sae.services.EmailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/email")
public class EmailController {

	private final EmailService emailService;

	public EmailController(EmailService emailService) {
		this.emailService = emailService;
	}

	@PostMapping("/send")
	public ResponseEntity<String> sendEmail(@RequestBody EmailSendDTO emailSendDTO) {
		boolean emailSent = emailService.sendEmail(
				emailSendDTO.getTo(),
				emailSendDTO.getSubject(),
				emailSendDTO.getBody(),
				emailSendDTO.getEmail(),
				emailSendDTO.getPassword()
		);

		if (emailSent) {

			return ResponseEntity.ok("Correo enviado correctamente");
		} else {
			return ResponseEntity.status(500).body("Error al enviar el correo");
		}
	}


	@PostMapping("/fetch")
	public ResponseEntity<List<String>> fetchEmails(@RequestBody Map<String, String> request) {
		String userEmail = request.get("userEmail");
		String userPassword = request.get("userPassword");

		List<String> emails = emailService.fetchEmails(userEmail, userPassword);
		return ResponseEntity.ok(emails);
	}
}
