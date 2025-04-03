package com.siscem.portal_sae.services;


import jakarta.mail.*;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import org.springframework.beans.factory.annotation.Value;
import org.jsoup.Jsoup;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.*;
import java.text.SimpleDateFormat;


@Service
public class EmailService {
    @Value("${attachment.storage.path:/tmp/attachments}")
    private String attachmentStoragePath;

    private void createStorageDirectoryIfNotExists() {
        File directory = new File(attachmentStoragePath);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    public boolean sendEmail(String to, String subject, String body, String userEmail, String userPassword, List<MultipartFile> attachments, String inReplyTo, String references ) {
        try {
            JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
            mailSender.setHost("smtp.hostinger.com");
            mailSender.setPort(465);
            mailSender.setUsername(userEmail);
            mailSender.setPassword(userPassword);

            Properties props = mailSender.getJavaMailProperties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.ssl.enable", "true");

            // Crear el mensaje
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(userEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);

            if (inReplyTo != null && !inReplyTo.isEmpty()) {
                message.setHeader("In-Reply-To", inReplyTo);
            }

            if (references != null && !references.isEmpty()) {
                message.setHeader("References", references);
            }

            if (attachments != null && !attachments.isEmpty()) {
                for (MultipartFile file : attachments) {
                    if (file != null && !file.isEmpty()) {
                        helper.addAttachment(file.getOriginalFilename(), file);
                    }
                }
            } else {
                System.out.println("No hay archivos adjuntos o la lista está vacía.");
            }

            mailSender.send(message);
            System.out.println("Correo enviado correctamente.");
            return true;
        } catch (Exception e) {
            System.err.println("Error al enviar el correo: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public List<String> fetchEmails(String email, String password) {
        List<String> emailList = new ArrayList<>();
        createStorageDirectoryIfNotExists();

        String host = "imap.hostinger.com";

        Properties props = new Properties();
        props.put("mail.store.protocol", "imaps");
        props.put("mail.imap.host", host);
        props.put("mail.imap.port", "993");
        props.put("mail.imap.ssl.enable", "true");
        props.put("mail.debug", "true");
        props.put("mail.imap.connectiontimeout", "5000");
        props.put("mail.imap.timeout", "10000");
        props.put("mail.imap.writetimeout", "5000");
        try {
            // Conectar a la sesión
            Session session = Session.getInstance(props);
            Store store = session.getStore("imaps");
            store.connect(host, email, password);
            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);

            Message[] messages = inbox.getMessages();

            for (int i = messages.length - 1; i >= 0; i--) {
                Message message = messages[i];
                StringBuilder emailDetails = new StringBuilder();

                String contents = Jsoup.parse(message.getContent().toString()).text();
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                String formattedDate = message.getReceivedDate() != null ? formatter.format(message.getReceivedDate()) : "No disponible";
                Address[] recipients = message.getAllRecipients();
                Address[] allRecipients = message.getAllRecipients();
                String toEmails = (allRecipients != null && allRecipients.length > 0)
                        ? Arrays.toString(allRecipients)
                        : "No disponible";

                // Obtener el Message-ID
                String messageId = "";
                String[] messageIdHeaders = message.getHeader("Message-ID");
                if (messageIdHeaders != null && messageIdHeaders.length > 0) {
                    messageId = messageIdHeaders[0];
                }
                // Obtener las referencias
                String references = "";
                String[] referencesHeaders = message.getHeader("References");
                if (referencesHeaders != null && referencesHeaders.length > 0) {
                    references = referencesHeaders[0];
                }
                // Obtener In-Reply-To
                String inReplyTo = "";
                String[] inReplyToHeaders = message.getHeader("In-Reply-To");
                if (inReplyToHeaders != null && inReplyToHeaders.length > 0) {
                    inReplyTo = inReplyToHeaders[0];
                }

                emailDetails.append("De: ").append(message.getFrom()[0])
                        .append(" - Destinatario: ").append(toEmails)
                        .append(" - Asunto: ").append(message.getSubject())
                        .append(" - Contenido: ").append(getTextFromMessage(message))
                        .append(" - Fecha de recibido: ").append(formattedDate)
                        .append(" - MessageID: ").append(messageId)
                        .append(" - References: ").append(references)
                        .append(" - InReplyTo: ").append(inReplyTo);

                if (message.isMimeType("multipart/*")) {
                    Multipart multipart = (Multipart) message.getContent();
                    for (int j = 0; j < multipart.getCount(); j++) {
                        BodyPart bodyPart = multipart.getBodyPart(j);
                        if (Part.ATTACHMENT.equalsIgnoreCase(bodyPart.getDisposition())) {
                            String fileName = bodyPart.getFileName();

                            // Generar un ID único para el archivo
                            String uniqueId = UUID.randomUUID().toString();
                            String storedFileName = uniqueId + "_" + fileName;
                            String filePath = attachmentStoragePath + File.separator + storedFileName;
                            saveAttachment(bodyPart, filePath);

                                emailDetails.append(" - Adjunto: ").append(fileName)
                                    .append(" - AdjuntoId: ").append(uniqueId);
                        }
                    }
                }
                emailList.add(emailDetails.toString());
            }
            inbox.close(false);
            store.close();
        } catch (AuthenticationFailedException authEx) {

            return List.of(" Imposible acceder: Credenciales incorrectas.");
        } catch (Exception e) {
            return List.of(" Error al acceder al servidor de correo. Intente más tarde.");
        }

        return emailList;
    }

    private void saveAttachment(BodyPart bodyPart, String filePath) throws Exception {
        try (InputStream is = bodyPart.getInputStream();
             FileOutputStream fos = new FileOutputStream(filePath)) {

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
        }
    }

    private static String getTextFromMessage(Message message) throws Exception {
        if (message.isMimeType("text/plain")) {
            return message.getContent().toString();
        } else if (message.isMimeType("multipart/*")) {
            Multipart multipart = (MimeMultipart) message.getContent();
            StringBuilder textContent = new StringBuilder();
            for (int i = 0; i < multipart.getCount(); i++) {
                BodyPart bodyPart = multipart.getBodyPart(i);
                if (bodyPart.isMimeType("text/plain")) {
                    textContent.append(bodyPart.getContent().toString());
                }
            }
            return textContent.toString();
        }
        return "";
    }
}
