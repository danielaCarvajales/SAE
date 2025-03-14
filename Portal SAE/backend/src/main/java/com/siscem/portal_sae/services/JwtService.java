package com.siscem.portal_sae.services;

import java.security.Key;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.siscem.portal_sae.dtos.user.UserLoginDTO;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

	// Value of the secret key used for JWT
	@Value("${jwt.token.secret}")
	private String secret;

	// Value of the secret key used for JWT
	private Key getSigningKey() {
	  byte[] keyBytes = Decoders.BASE64.decode(secret);
	  return Keys.hmacShaKeyFor(keyBytes);
	}
	
	// Validates the provided JWT token
	public boolean validateToken(String token) {
		try {
			Jwts.parserBuilder()
					.setSigningKey(getSigningKey())
					.build()
					.parseClaimsJws(token);
			return true;
		}catch (JwtException | IllegalArgumentException e) {
			return false;
		}
	}
	
	public String getToken(UserLoginDTO userLoginDTO) {
		return Jwts.builder()
				.setId(UUID.randomUUID().toString())
				.setSubject(userLoginDTO.getNombre())
				.claim("email", userLoginDTO.getEmail())
				.signWith(getSigningKey())
				.compact();
	}
	
}
