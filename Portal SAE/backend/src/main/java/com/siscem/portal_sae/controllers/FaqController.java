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

import com.siscem.portal_sae.dtos.faq.FaqCreateDTO;
import com.siscem.portal_sae.dtos.faq.FaqDTO;
import com.siscem.portal_sae.dtos.faq.FaqEditDTO;
import com.siscem.portal_sae.services.FaqService;
import com.siscem.portal_sae.services.JwtService;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/faq")
public class FaqController {

	@Autowired
	FaqService faqService;

	@Autowired
	private JwtService jwtService;

	@Operation(summary = "Returns a list of all FAQs inside the database.")
	@GetMapping
	public ResponseEntity<List<FaqDTO>> getAllFaqs(@RequestHeader("Authorization") String authorizationHeader) {
		if (jwtService.validateToken(authorizationHeader)) {
			return faqService.getAllFaqs();
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}

	@Operation(summary = "Creates a FAQ within the database.")
	@PostMapping
	public ResponseEntity<String> createFaq(@RequestHeader("Authorization") String authorizationHeader,
			@RequestBody FaqCreateDTO faq) {
		if (jwtService.validateToken(authorizationHeader)) {
			return faqService.createFaq(faq);
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}

	@Operation(summary = "Updates a FAQ within the database.")
	@PutMapping("/{faqCode}")
	public ResponseEntity<String> editFaq(@RequestHeader("Authorization") String authorizationHeader,
			@PathVariable Integer faqCode, @RequestBody FaqEditDTO faq) {
		if (jwtService.validateToken(authorizationHeader)) {
			return faqService.editFaq(faqCode, faq);
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}
}
