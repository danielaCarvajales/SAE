package com.siscem.portal_sae.services;

// Importación de bibliotecas
import java.util.*;
import java.util.stream.Collectors;
import javax.mail.*;
import javax.mail.internet.MimeMultipart;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.siscem.portal_sae.dtos.email.EmailDTO;
import com.siscem.portal_sae.models.Email;
import com.siscem.portal_sae.models.Event;
import com.siscem.portal_sae.repositories.EmailRepository;
import com.siscem.portal_sae.repositories.EventRepository;
import com.siscem.portal_sae.repositories.UserRepository;

@Service
public class EmailService {

	private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

	@Autowired
	private EmailRepository emailRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private EventRepository eventRepository;

	public ResponseEntity<String> retrieveAndSaveEmails(String host, String userEmail, String password, String protocol,
														String port, Integer userCode) {
		logger.info("Iniciando la recuperación de correos para el usuario: {}", userEmail);

		Properties properties = new Properties();
		properties.setProperty("mail.store.protocol", protocol);
		properties.setProperty("mail.imap.host", host);
		properties.setProperty("mail.imap.port", port);
		properties.setProperty("mail.imap.ssl.enable", "true");

		try (Store store = Session.getInstance(properties).getStore()) {
			logger.info("Conectando al servidor de correos: {} mediante protocolo: {}", host, protocol);
			store.connect(host, userEmail, password);

			Folder inbox = store.getFolder("INBOX");
			inbox.open(Folder.READ_ONLY);
			logger.info("Accediendo a la bandeja de entrada del usuario: {}", userEmail);

			Message[] messages = inbox.getMessages();
			logger.info("Número de mensajes recuperados: {}", messages.length);

			Map<String, List<Message>> conversations = new HashMap<>();

			for (Message message : messages) {
				try {
					String threadId = Optional.ofNullable(message.getHeader("Message-ID"))
							.map(headers -> headers[0])
							.orElse(UUID.randomUUID().toString());
					conversations.computeIfAbsent(threadId, k -> new ArrayList<>()).add(message);
				} catch (Exception ex) {
					logger.warn("Error procesando un mensaje: {}", ex.getMessage());
				}
			}
			for (List<Message> conversation : conversations.values()) {
				for (Message message : conversation) {
					try {
						String messageId = message.getHeader("Message-ID")[0];
						Optional<Email> existingEmailOptional = emailRepository.findById(messageId);

						if (existingEmailOptional.isPresent()) {
							Email existingEmail = existingEmailOptional.get();
							existingEmail.setContenido(getContentAsString(message));
							emailRepository.save(existingEmail);
						} else {
							Email email = new Email();
							email.setRemitente(Arrays.toString(message.getFrom()));
							email.setDestinatario(Arrays.toString(message.getAllRecipients()));
							email.setAsunto(message.getSubject());
							email.setContenido(getContentAsString(message));
							email.setCodigo(messageId);
							email.setFechaEnviado(message.getSentDate());
							email.setFechaRecibido(message.getReceivedDate());
							email.setUsuarioAsignado(userRepository.findById(userCode).orElse(null));
							emailRepository.save(email);
						}
					} catch (Exception ex) {
						logger.error("Error procesando el mensaje con ID: {} - {}", message.getHeader("Message-ID"), ex.getMessage());
					}
				}
			}

			inbox.close(false);
			return ResponseEntity.ok("Correos electrónicos obtenidos con éxito.");
		} catch (MessagingException me) {
			logger.error("Error al conectar o procesar correos: {}", me.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al obtener los correos electrónicos.");
		} catch (Exception ex) {
			logger.error("Error general en el servicio de correos: {}", ex.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error inesperado al obtener correos.");
		}
	}

	private String getContentAsString(Message message) throws Exception {
		Object content = message.getContent();
		if (content instanceof MimeMultipart) {
			MimeMultipart multipart = (MimeMultipart) content;
			StringBuilder textContent = new StringBuilder();
			for (int i = 0; i < multipart.getCount(); i++) {
				BodyPart bodyPart = multipart.getBodyPart(i);
				if (bodyPart.isMimeType("text/plain")) {
					textContent.append(bodyPart.getContent());
				}
			}
			return textContent.toString();
		}
		return content.toString();
	}

	public ResponseEntity<List<EmailDTO>> getAllUserEmails(Integer userCode) {
		List<Email> emails = emailRepository.findByUsuarioAsignadoCodigo(userCode);

		// Ordenar por fecha de recibido en orden descendente
		emails.sort((e1, e2) -> e2.getFechaRecibido().compareTo(e1.getFechaRecibido()));

		ModelMapper modelMapper = new ModelMapper();
		TypeMap<Email, EmailDTO> typeMap = modelMapper.createTypeMap(Email.class, EmailDTO.class);
		typeMap.addMappings(mp -> {
			mp.skip(EmailDTO::setEventoAsignado);
		});

		return ResponseEntity.ok(emails.stream().map(email -> {
			EmailDTO emailDTO = modelMapper.map(email, EmailDTO.class);
			if (email.getEventoAsignado() == null) {
				emailDTO.setEventoAsignado(null);
			} else {
				emailDTO.setEventoAsignado(email.getEventoAsignado().getAsunto());
			}
			return emailDTO;
		}).collect(Collectors.toList()));
	}


	/**
	 * Retrieves all emails associated with a given event code.
	 *
	 * @param userCode The code of the event to retrieve emails for.
	 * @return ResponseEntity<List<EmailDTO>> An HTTP response containing a list of
	 *         EmailDTO objects representing the emails associated with the event.
	 *         Returns ResponseEntity with status OK and a list of EmailDTOs if
	 *         emails are found for the event.
	 */
	public ResponseEntity<List<EmailDTO>> getAllEventEmails(Integer userCode) {
		List<Email> emails = emailRepository.findByUsuarioAsignadoCodigoOrderByFechaRecibidoDesc(userCode);

		ModelMapper modelMapper = new ModelMapper();
		return ResponseEntity.ok(
				emails.stream()
						.map(email -> modelMapper.map(email, EmailDTO.class))
						.collect(Collectors.toList())
		);
	}

	/**
	 * Updates the assigned events for a given event code and list of email codes.
	 *
	 * @param eventCode  The code of the event to update.
	 * @param emailCodes The list of email codes to assign to the event.
	 * @return ResponseEntity<String> An HTTP response indicating the result of the
	 *         operation. Returns ResponseEntity with status OK and a success
	 *         message if the event and emails are found and updated. Returns
	 *         ResponseEntity with status NOT_FOUND and an error message if the
	 *         event or any email is not found. Returns ResponseEntity with status
	 *         INTERNAL_SERVER_ERROR and an error message if an unexpected error
	 *         occurs.
	 */
	public ResponseEntity<String> updateAssignedEvents(Integer eventCode, List<String> emailCodes) {
		try {
			Optional<Event> eventOptional = eventRepository.findById(eventCode);
			if (eventOptional.isPresent()) {
				Event event = eventOptional.get();

				for (String emailCode : emailCodes) {
					Optional<Email> emailOptional = emailRepository.findById(emailCode);
					if (emailOptional.isPresent()) {
						Email email = emailOptional.get();
						email.setEventoAsignado(event);
						emailRepository.save(email);
					} else {
						return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Email no encontrado: " + emailCode);
					}
				}

				return ResponseEntity.ok("Eventos asignados actualizados");
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Evento no encontrado");
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error al actualizar los eventos asignados");
		}
	}

	public List<Email> buscarCorreosPorRemitente(String nombreRemitente) {
		List<Email> emails = emailRepository.findAll();
		return emails.stream()
				.filter(email -> extraerNombreRemitente(email.getRemitente()).equalsIgnoreCase(nombreRemitente))
				.collect(Collectors.toList());
	}


	private String extraerNombreRemitente(String remitente) {

		String nombre = remitente.split("<")[0].trim();
		return nombre;
	}

}
