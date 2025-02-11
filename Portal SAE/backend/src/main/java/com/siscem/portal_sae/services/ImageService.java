package com.siscem.portal_sae.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImageService {

	private String uploadDirectory;

	public ImageService() {
		this.uploadDirectory = Paths.get(System.getProperty("user.dir"), "images").toString();
	}

	/**
	 * Retrieves the image with the specified file name.
	 * 
	 * @param fileName The name of the image file to retrieve.
	 * @return ResponseEntity containing the image resource if found, or a NOT_FOUND
	 *         status if the image does not exist.
	 */
	public ResponseEntity<Resource> getImage(String fileName) {
		try {
			Path filePath = Paths.get(uploadDirectory).resolve(fileName).normalize();
			Resource resource = new UrlResource(filePath.toUri());
			if (resource.exists()) {
				return ResponseEntity.ok().header("Content-Disposition", "inline; filename=" + fileName).body(resource);
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
			}
		} catch (IOException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	/**
	 * Uploads an image file to the server.
	 * 
	 * @param file The MultipartFile representing the image file to upload.
	 * @return ResponseEntity containing the unique file name of the uploaded image,
	 *         or an INTERNAL_SERVER_ERROR status if an error occurs during upload.
	 */
	public ResponseEntity<String> uploadImage(MultipartFile file) {
		try {
			Path uploadPath = Paths.get(uploadDirectory);
			if (!Files.exists(uploadPath)) {
				Files.createDirectories(uploadPath);
				System.out.println("Folder 'imagenes' created at: " + uploadPath.toAbsolutePath());
			}

			String uniqueFileName = generateUniqueFileName(file.getOriginalFilename());
			Path filePath = Paths.get(uploadDirectory, uniqueFileName);
			Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

			return ResponseEntity.ok(uniqueFileName);
		} catch (IOException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading the file");
		}
	}

	/**
	 * Generates a unique file name for an uploaded image.
	 * 
	 * @param originalFileName The original file name of the uploaded image.
	 * @return The unique file name for the uploaded image, incorporating a
	 *         timestamp to ensure uniqueness.
	 */
	private String generateUniqueFileName(String originalFileName) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		String timestamp = dateFormat.format(new Date());

		int lastIndex = originalFileName.lastIndexOf(".");
		String extension = lastIndex != -1 ? originalFileName.substring(lastIndex) : "";
		return timestamp + "_" + originalFileName.replace(extension, "") + extension;
	}

}
