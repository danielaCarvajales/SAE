package com.siscem.portal_sae.services;


import jakarta.mail.*;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import org.jsoup.Jsoup;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Service
public class EmailService {

    public boolean sendEmail(String to, String subject, String body, String userEmail, String userPassword) {
        try {
            // Configurar JavaMailSender con credenciales dinámicas
            JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
            mailSender.setHost("smtp.hostinger.com"); // Cambia según tu proveedor SMTP
            mailSender.setPort(465); // Puerto seguro de Hostinger
            mailSender.setUsername(userEmail);
            mailSender.setPassword(userPassword);

            Properties props = mailSender.getJavaMailProperties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.ssl.enable", "true"); // Importante para SMTP seguro

            // Crear el mensaje
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(userEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);

            // Enviar el correo
            mailSender.send(message);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<String> fetchEmails(String userEmail, String userPassword) {
        List<String> emailList = new ArrayList<>();

        String host = "imap.hostinger.com"; // Cambia a IMAP

        Properties props = new Properties();
        props.put("mail.store.protocol", "imaps"); // IMAP seguro
        props.put("mail.imap.host", host);
        props.put("mail.imap.port", "993"); // Puerto IMAP seguro
        props.put("mail.imap.ssl.enable", "true");
        props.put("mail.debug", "true"); // Debug para ver errores

        try {
            // Conectar a la sesión
            Session session = Session.getInstance(props);
            Store store = session.getStore("imaps");
            store.connect(host, userEmail, userPassword);

            // Abrir la bandeja de entrada
            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);

            // Obtener los últimos 10 correos
            int count = inbox.getMessageCount();
            int start = Math.max(1, count - 10); // Evita índices negativos
            Message[] messages = inbox.getMessages(start, count);

            for (int i = messages.length - 1; i >= 0; i--) {
                Message message = messages[i];
                String contents = Jsoup.parse(message.getContent().toString()).text();

                emailList.add("De: " + message.getFrom()[0] + " - Asunto: " + message.getSubject() + " - Contenido: " + contents);
            }

            inbox.close(false);
            store.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return emailList;
    }

    private static String getTextFromMessage(Message message) throws Exception {
        if (message.isMimeType("text/plain")) {
            return message.getContent().toString();
        } else if (message.isMimeType("multipart/*")) {
            Multipart multipart = (MimeMultipart) message.getContent();
            return multipart.getBodyPart(0).getContent().toString();
        }
        return "";
    }
}
