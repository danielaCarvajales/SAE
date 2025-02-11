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

import com.siscem.portal_sae.dtos.account.AccountCreateDTO;
import com.siscem.portal_sae.dtos.account.AccountDTO;
import com.siscem.portal_sae.dtos.account.AccountEditDTO;
import com.siscem.portal_sae.services.AccountService;
import com.siscem.portal_sae.services.JwtService;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("cuenta")
public class AccountController {

	@Autowired
	AccountService accountService;

	@Autowired
	private JwtService jwtService;

	@Operation(summary = "Returns a list of the user's accounts given their code.")
	@GetMapping("/usuario/{userCode}")
	public ResponseEntity<List<AccountDTO>> getAccountsForUser(
			@RequestHeader("Authorization") String authorizationHeader, @PathVariable Integer userCode) {
		if (jwtService.validateToken(authorizationHeader)) {
			return accountService.getAccountsForUser(userCode);
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}

	@Operation(summary = "Creates an account within the database.")
	@PostMapping
	public ResponseEntity<String> createAccount(@RequestHeader("Authorization") String authorizationHeader,
			@RequestBody AccountCreateDTO account) {
		if (jwtService.validateToken(authorizationHeader)) {
			return accountService.createAccount(account);
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}

	@Operation(summary = "Updates an account within the database.")
	@PutMapping("/{accountCode}")
	public ResponseEntity<String> editAccount(@RequestHeader("Authorization") String authorizationHeader,
			@PathVariable Integer accountCode, @RequestBody AccountEditDTO account) {
		if (jwtService.validateToken(authorizationHeader)) {
			return accountService.editAccount(accountCode, account);
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}

	@Operation(summary = "Deletes an account within the database.")
	@DeleteMapping("/{accountCode}")
	public ResponseEntity<String> deleteAccount(@RequestHeader("Authorization") String authorizationHeader,
			@PathVariable Integer accountCode) {
		if (jwtService.validateToken(authorizationHeader)) {
			return accountService.deleteAccount(accountCode);
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}

}
