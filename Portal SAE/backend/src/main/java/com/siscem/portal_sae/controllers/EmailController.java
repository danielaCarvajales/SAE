package com.siscem.portal_sae.controllers;

import com.siscem.portal_sae.dtos.email.LoginRequestDTO;
import com.siscem.portal_sae.services.EmailService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/email")

public class EmailController {
	@Value("${attachment.storage.path:/tmp/attachments}")
	private String attachmentStoragePath;

	private final EmailService emailService;

	public EmailController(EmailService emailService) {
		this.emailService = emailService;
	}

	@PostMapping(value = "/send", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<String> sendEmail(
			@RequestPart("to") String to,
			@RequestPart("subject") String subject,
			@RequestPart("body") String body,
			@RequestPart("email") String userEmail,
			@RequestPart("password") String userPassword,
			@RequestPart(value = "attachments", required = false) List<MultipartFile> attachments) {

		try {
			boolean emailSent = emailService.sendEmail(to, subject, body, userEmail, userPassword, attachments);

			if (emailSent) {
				return ResponseEntity.ok("Correo enviado correctamente");
			} else {
				return ResponseEntity.status(500).body("Error al enviar el correo");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(500).body("Error en el servidor: " + e.getMessage());
		}
	}


	@PostMapping("/fetch")
	public ResponseEntity<List<String>> fetchEmails(@RequestBody LoginRequestDTO request) {
		String userEmail = request.getEmail();
		String userPassword = request.getPassword();
		return ResponseEntity.ok(emailService.fetchEmails(userEmail, userPassword));
	}

	@RequestMapping(value = "/fetch", method = RequestMethod.OPTIONS)
	public ResponseEntity<?> handleOptions() {
		return ResponseEntity.ok().build();
	}

	@GetMapping("/attachment/{attachmentId}/{fileName}")
	public ResponseEntity<Resource> downloadAttachment(
			@PathVariable String attachmentId,
			@PathVariable String fileName) {
		try {
			String storedFileName = attachmentId + "_" + fileName;
			Path filePath = Paths.get(attachmentStoragePath, storedFileName);

			if (!Files.exists(filePath)) {
				try (var files = Files.list(Paths.get(attachmentStoragePath))) {
					Optional<Path> matchingFile = files
							.filter(file -> file.getFileName().toString().startsWith(attachmentId))
							.findFirst();
					if (matchingFile.isPresent()) {
						String originalFileName = filePath.getFileName().toString();
						int underscoreIndex = originalFileName.indexOf('_');
						if (underscoreIndex >= 0 && underscoreIndex < originalFileName.length() - 1) {
							fileName = originalFileName.substring(underscoreIndex + 1);
						}
					} else {
						return ResponseEntity.notFound().build();
					}
				}
			}

			Resource resource = new FileSystemResource(filePath.toFile());
			String contentType = determineContentType(fileName);
			if (!fileName.contains(".")) {
				String extension = getExtensionFromContentType(contentType);
				if (extension != null && !extension.isEmpty()) {
					fileName = fileName + "." + extension;
				}
			}

			return ResponseEntity.ok()
					.contentType(MediaType.parseMediaType(contentType))
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
					.body(resource);

		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError().build();
		}
	}

	private String determineContentType(String fileName) {
		String extension = "";
		int i = fileName.lastIndexOf('.');
		if (i > 0) {
			extension = fileName.substring(i + 1).toLowerCase();
		}
		switch (extension) {
			case "doc":
				return "application/msword";
			case "docx":
				return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
			case "xls":
				return "application/vnd.ms-excel";
			case "xlsx":
				return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
			case "ppt":
				return "application/vnd.ms-powerpoint";
			case "pptx":
				return "application/vnd.openxmlformats-officedocument.presentationml.presentation";
			case "pdf":
				return "application/pdf";
			case "txt":
				return "text/plain";
			case "jpg":
			case "jpeg":
				return "image/jpeg";
			case "png":
				return "image/png";
			case "gif":
				return "image/gif";
			default:
				try {
					String probed = Files.probeContentType(Paths.get(fileName));
					return probed != null ? probed : "application/octet-stream";
				} catch (IOException e) {
					return "application/octet-stream";
				}
		}
	}

	private String getExtensionFromContentType(String contentType) {
		switch (contentType) {
			case "application/msword":
				return "doc";
			case "application/vnd.openxmlformats-officedocument.wordprocessingml.document":
				return "docx";
			case "application/vnd.ms-excel":
				return "xls";
			case "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet":
				return "xlsx";
			case "application/vnd.ms-powerpoint":
				return "ppt";
			case "application/vnd.openxmlformats-officedocument.presentationml.presentation":
				return "pptx";
			case "application/pdf":
				return "pdf";
			case "text/plain":
				return "txt";
			case "image/jpeg":
				return "jpg";
			case "image/png":
				return "png";
			case "image/gif":
				return "gif";
			default:
				return "";
		}
	}
}
