package com.siscem.portal_sae.services;

import jakarta.mail.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.Properties;

@Service
@RequiredArgsConstructor

public class AuthService {

    public boolean authenticate(String email, String contrasena) {
        return validateSMTPLogin(email, contrasena);
    }

    public boolean validateSMTPLogin(String email, String contrasena) {
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.host", "smtp.hostinger.com");
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.ssl.trust", "smtp.hostinger.com");

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(email, contrasena);
            }
        });

        try {
            Transport transport = session.getTransport("smtp");
            transport.connect("smtp.hostinger.com", email, contrasena);
            transport.close();
            return true;
        } catch (MessagingException e) {
            return false;
        }
    }
}
