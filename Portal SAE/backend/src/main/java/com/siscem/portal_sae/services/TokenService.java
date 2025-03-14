package com.siscem.portal_sae.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Date;

@Service
public class TokenService {

    private static final String SECRET = "portalSae_estaessuclave_estetienequepermanecer";
    private static final byte[] SECRET_BYTES = SECRET.getBytes();
    private static final SecretKey SECRET_KEY = new SecretKeySpec(SECRET_BYTES, "HmacSHA256");

    public String getEmailToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .claim("scope", "email_access")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis()+ 86400000))
                .signWith( SECRET_KEY ,SignatureAlgorithm.HS256)
                .compact();
        }
    public Claims getUserFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
