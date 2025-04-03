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
			@RequestPart(value = "attachments", required = false) List<MultipartFile> attachments,
	 		@RequestPart(value = "inReplyTo", required = false) String inReplyTo,
			@RequestPart(value = "references", required = false) String references)

	{

		try {
			boolean emailSent = emailService.sendEmail(to, subject, body, userEmail, userPassword, attachments, inReplyTo, references);

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
			// Imprimir información de depuración
			System.out.println("Solicitando archivo: ID=" + attachmentId + ", Nombre=" + fileName);
			System.out.println("Directorio de almacenamiento: " + attachmentStoragePath);

			String storedFileName = attachmentId + "_" + fileName;
			Path filePath = Paths.get(attachmentStoragePath, storedFileName);
			Path actualFilePath = filePath;

			// Verificar si el archivo existe exactamente como se espera
			if (!Files.exists(filePath)) {
				System.out.println("Archivo no encontrado en la ruta exacta: " + filePath);
				System.out.println("Buscando archivos que comiencen con: " + attachmentId);

				// Buscar cualquier archivo que comience con el ID del adjunto
				try (var files = Files.list(Paths.get(attachmentStoragePath))) {
					Optional<Path> matchingFile = files
							.filter(file -> file.getFileName().toString().startsWith(attachmentId))
							.findFirst();

					if (matchingFile.isPresent()) {
						actualFilePath = matchingFile.get();
						System.out.println("Archivo encontrado: " + actualFilePath);

						String originalFileName = actualFilePath.getFileName().toString();
						int underscoreIndex = originalFileName.indexOf('_');
						if (underscoreIndex >= 0 && underscoreIndex < originalFileName.length() - 1) {
							fileName = originalFileName.substring(underscoreIndex + 1);
							System.out.println("Nombre de archivo extraído: " + fileName);
						}
					} else {
						System.out.println("No se encontró ningún archivo que comience con: " + attachmentId);

						// Listar todos los archivos en el directorio para depuración
						System.out.println("Archivos disponibles en el directorio:");
						try (var allFiles = Files.list(Paths.get(attachmentStoragePath))) {
							allFiles.forEach(file -> System.out.println("- " + file.getFileName()));
						}

						return ResponseEntity.notFound().build();
					}
				}
			} else {
				System.out.println("Archivo encontrado en la ruta exacta: " + filePath);
			}

			// CORRECCIÓN IMPORTANTE: Usar actualFilePath en lugar de filePath
			Resource resource = new FileSystemResource(actualFilePath.toFile());

			// Verificar que el archivo existe y tiene contenido
			if (!resource.exists() || resource.contentLength() == 0) {
				System.out.println("El recurso no existe o está vacío");
				return ResponseEntity.notFound().build();
			}

			System.out.println("Tamaño del archivo: " + resource.contentLength() + " bytes");

			String contentType = determineContentType(fileName);
			System.out.println("Tipo de contenido determinado: " + contentType);

			// Para archivos sin extensión, añadir la extensión adecuada
			if (!fileName.contains(".")) {
				String extension = getExtensionFromContentType(contentType);
				if (extension != null && !extension.isEmpty()) {
					fileName = fileName + "." + extension;
					System.out.println("Añadida extensión al nombre de archivo: " + fileName);
				}
			}

			// Configurar encabezados para forzar la descarga
			return ResponseEntity.ok()
					.contentType(MediaType.parseMediaType(contentType))
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
					.header(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate")
					.header(HttpHeaders.PRAGMA, "no-cache")
					.header(HttpHeaders.EXPIRES, "0")
					.body(resource);

		} catch (Exception e) {
			System.out.println("Error al procesar la solicitud de descarga: " + e.getMessage());
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

				return "application/octet-stream";

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
