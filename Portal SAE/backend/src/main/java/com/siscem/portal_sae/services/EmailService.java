package com.siscem.portal_sae.services;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import com.siscem.portal_sae.models.Email;
import com.siscem.portal_sae.models.User;
import com.siscem.portal_sae.repositories.EmailRepository;
import com.siscem.portal_sae.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Properties;

@Service
public class EmailService {

	@Autowired
	private GmailAuthService gmailAuthService;

	@Autowired
	private EmailRepository emailRepository;

	@Autowired
	private UserRepository userRepository;

	public ResponseEntity<String> sendEmail(String to, String subject, String body, Integer userCode) {
		try {
			Gmail gmail = gmailAuthService.getGmailService();
			User user = userRepository.findById(userCode).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

			MimeMessage email = createEmail(user.getEmail(), to, subject, body);
			Message message = createMessageWithEmail(email);
			message = gmail.users().messages().send("me", message).execute();

			// Guardar el correo enviado en la base de datos
			Email emailEntity = new Email();
			emailEntity.setCodigo(message.getId());
			emailEntity.setRemitente(user.getEmail());
			emailEntity.setDestinatario(to);
			emailEntity.setAsunto(subject);
			emailEntity.setContenido(body);
			emailEntity.setFechaEnviado(new java.util.Date());
			emailEntity.setUsuarioAsignado(user);
			emailRepository.save(emailEntity);

			return ResponseEntity.ok("Correo enviado con éxito.");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al enviar el correo: " + e.getMessage());
		}
	}

	private MimeMessage createEmail(String from, String to, String subject, String bodyText) throws MessagingException {
		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props, null);

		MimeMessage email = new MimeMessage(session);
		email.setFrom(new InternetAddress(from));
		email.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(to));
		email.setSubject(subject);
		email.setText(bodyText);
		return email;
	}

	private Message createMessageWithEmail(MimeMessage emailContent) throws MessagingException, IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		emailContent.writeTo(buffer);
		byte[] bytes = buffer.toByteArray();
		String encodedEmail = Base64.getUrlEncoder().encodeToString(bytes);
		Message message = new Message();
		message.setRaw(encodedEmail);
		return message;
	}

	public ResponseEntity<List<Email>> getEmails(Integer userCode) {
		User user = userRepository.findById(userCode).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
		List<Email> emails = emailRepository.findByUsuarioAsignadoCodigo(userCode);
		return ResponseEntity.ok(emails);
	}
}