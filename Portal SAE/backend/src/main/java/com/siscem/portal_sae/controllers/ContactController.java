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

import com.siscem.portal_sae.dtos.contact.ContactCreateDTO;
import com.siscem.portal_sae.dtos.contact.ContactDTO;
import com.siscem.portal_sae.dtos.contact.ContactEditDTO;
import com.siscem.portal_sae.services.ContactService;
import com.siscem.portal_sae.services.JwtService;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/contacto")
public class ContactController {

	@Autowired
	ContactService contactService;

	@Autowired
	private JwtService jwtService;

	@Operation(summary = "Returns a list of contacts given the account's code.")
	@GetMapping("/cuenta/{accountCode}")
	public ResponseEntity<List<ContactDTO>> getContactsForAccount(
			@RequestHeader("Authorization") String authorizationHeader, @PathVariable Integer accountCode) {
		if (jwtService.validateToken(authorizationHeader)) {
			return contactService.getContactsForAccount(accountCode);
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}

	@Operation(summary = "Creates a contact within the database.")
	@PostMapping
	public ResponseEntity<String> createContact(@RequestHeader("Authorization") String authorizationHeader,
			@RequestBody ContactCreateDTO contact) {
		if (jwtService.validateToken(authorizationHeader)) {
			return contactService.createContact(contact);
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}

	@Operation(summary = "Updates a contact within the database.")
	@PutMapping("/{contactCode}")
	public ResponseEntity<String> editContact(@RequestHeader("Authorization") String authorizationHeader,
			@PathVariable Integer contactCode, @RequestBody ContactEditDTO contact) {
		if (jwtService.validateToken(authorizationHeader)) {
			return contactService.editContact(contactCode, contact);
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}

	@Operation(summary = "Deletes a contact within the database.")
	@DeleteMapping("/{contactCode}")
	public ResponseEntity<String> deleteContact(@RequestHeader("Authorization") String authorizationHeader,
			@PathVariable Integer contactCode) {
		if (jwtService.validateToken(authorizationHeader)) {
			return contactService.deleteContact(contactCode);
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}
}
