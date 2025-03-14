package com.siscem.portal_sae.controllers;


import com.siscem.portal_sae.dtos.email.EmailSendDTO;
import com.siscem.portal_sae.services.AuthService;
import com.siscem.portal_sae.services.EmailService;
import com.siscem.portal_sae.services.TokenService;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/email")
public class EmailController {

	@Autowired
	EmailService emailService;

	@Autowired
	private TokenService tokenService;
    @Autowired
    private AuthService authService;


	@PostMapping("/send")
	public ResponseEntity<String> sendEmail(
			@RequestHeader("Authorization") String token,
			@RequestBody EmailSendDTO emailSendDTO) {
		try {
			Claims claims = tokenService.getUserFromToken(token.replace("Bearer ", ""));

			if (!"email_access".equals(claims.get("scope"))) {
				return ResponseEntity.status(403).body("Token no válido para enviar correos");
			}

			String userEmail = claims.getSubject();
			emailService.sendEmail(emailSendDTO.getTo(), emailSendDTO.getSubject(),emailSendDTO.getBody(), userEmail, "userPassword");
			return ResponseEntity.ok("Correo enviado correctamente");
		}
		catch (Exception e) {
			return ResponseEntity.status(401).body("Token inválido o expirado");
		}
	}
}