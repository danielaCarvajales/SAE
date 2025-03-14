package com.siscem.portal_sae.controllers;


import com.siscem.portal_sae.dtos.email.EmailAuthDTO;
import com.siscem.portal_sae.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/authentication")
public class AuthController {
    @Autowired
    private AuthService authService;


    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody EmailAuthDTO emailAuthDTO) {
        boolean authenticated = authService.authenticate(emailAuthDTO.getEmail(),emailAuthDTO.getContrasena());
        if(authenticated) {
            return ResponseEntity.ok("Inicio de sesión exitoso");
        }else {
            return  ResponseEntity.status(401).body("Credenciales incorrectas en Hostinger");
        }
    }
}
