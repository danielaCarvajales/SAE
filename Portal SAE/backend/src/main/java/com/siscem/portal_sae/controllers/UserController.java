package com.siscem.portal_sae.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.siscem.portal_sae.dtos.user.UserByRoleDTO;
import com.siscem.portal_sae.dtos.user.UserLoginRequestDTO;
import com.siscem.portal_sae.dtos.user.UsersDTO;
import com.siscem.portal_sae.services.JwtService;
import com.siscem.portal_sae.services.UserService;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/usuario")
public class UserController {

	@Autowired
	UserService userService;

	@Autowired
	private JwtService jwtService;

	@Operation(summary = "Returns a list of all users")
	@GetMapping
	public ResponseEntity<List<UsersDTO>> getAllUsers(@RequestHeader("Authorization") String authorizationHeader) {
		if (jwtService.validateToken(authorizationHeader)) {
			return userService.getAllUsers();
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}

	@Operation(summary = "Returns a list of users with the specified role.")
	@GetMapping("/{role}")
	public ResponseEntity<List<UserByRoleDTO>> getUsersByRole(
			@RequestHeader("Authorization") String authorizationHeader, @PathVariable("role") String role) {
		if (jwtService.validateToken(authorizationHeader)) {
			return userService.getUsersByRole(role);
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}

	@Operation(summary = "Returns an user if the name and password match.")
	@PostMapping("/inicio_sesion")
	public ResponseEntity<String> loginUser(@RequestBody UserLoginRequestDTO request) {
		return userService.loginUser(request.getNombre(), request.getContrasena());
	}

}
