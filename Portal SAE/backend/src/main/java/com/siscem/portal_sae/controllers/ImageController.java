package com.siscem.portal_sae.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.siscem.portal_sae.services.ImageService;
import com.siscem.portal_sae.services.JwtService;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/imagen")
public class ImageController {

	@Autowired
	private ImageService imageService;

	@Autowired
	private JwtService jwtService;

	@Operation(summary = "Retrieve an image by file name")
	@GetMapping("/{fileName}")
	public ResponseEntity<Resource> getImage(@PathVariable String fileName) {
		return imageService.getImage(fileName);
	}

	@Operation(summary = "Upload an image file")
	@PostMapping
	public ResponseEntity<String> uploadImage(@RequestHeader("Authorization") String authorizationHeader,
			@RequestPart("file") MultipartFile file) {
		if (jwtService.validateToken(authorizationHeader)) {
			return imageService.uploadImage(file);
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}
}
