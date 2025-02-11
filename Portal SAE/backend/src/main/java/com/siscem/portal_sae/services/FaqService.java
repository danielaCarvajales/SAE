package com.siscem.portal_sae.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.siscem.portal_sae.dtos.faq.FaqCreateDTO;
import com.siscem.portal_sae.dtos.faq.FaqDTO;
import com.siscem.portal_sae.dtos.faq.FaqEditDTO;
import com.siscem.portal_sae.models.Faq;
import com.siscem.portal_sae.repositories.FaqRepository;
import com.siscem.portal_sae.repositories.UserRepository;
import com.siscem.portal_sae.repositories.Relationships.FaqCategoriesRepository;

@Service
public class FaqService {

	@Autowired
	FaqRepository faqRepository;

	@Autowired
	FaqCategoriesRepository faqCategoriesRepository;

	@Autowired
	UserRepository userRepository;

	/**
	 * Retrieves all FAQs from the database and converts them into FaqDTO objects.
	 * 
	 * @return List<FaqDTO> A list of FaqDTO objects representing all FAQs.
	 */
	public ResponseEntity<List<FaqDTO>> getAllFaqs() {
		List<Faq> faqs = faqRepository.findAll();

		ModelMapper modelMapper = new ModelMapper();
		TypeMap<Faq, FaqDTO> typeMap = modelMapper.createTypeMap(Faq.class, FaqDTO.class);
		typeMap.addMappings(mp -> {
			mp.skip(FaqDTO::setCategoria);
			mp.skip(FaqDTO::setPreguntaRealizadaPor);
			mp.skip(FaqDTO::setRespuestaRealizadaPor);
		});

		return ResponseEntity.ok(faqs.stream().map(faq -> {
			FaqDTO faqDTO = modelMapper.map(faq, FaqDTO.class);
			faqDTO.setCategoria(faq.getCategoria().getNombreCategoria());
			faqDTO.setPreguntaRealizadaPor(faq.getPreguntaRealizadaPor().getNombre());
			faqDTO.setRespuestaRealizadaPor(faq.getRespuestaRealizadaPor().getNombre());
			return faqDTO;
		}).collect(Collectors.toList()));
	}

	/**
	 * Creates a new FAQ based on the provided FaqCreateDTO.
	 * 
	 * @param createFaqDTO The FaqCreateDTO containing information for creating the
	 *                     FAQ.
	 * @return ResponseEntity<String> An HTTP response indicating the result of the
	 *         operation. Returns ResponseEntity with status CREATED and a message
	 *         "FAQ Creada" if the FAQ is successfully created.
	 */
	public ResponseEntity<String> createFaq(FaqCreateDTO createFaqDTO) {
		ModelMapper modelMapper = new ModelMapper();
		Faq faq = modelMapper.map(createFaqDTO, Faq.class);
		faq.setCategoria(faqCategoriesRepository.findById(createFaqDTO.getCategoria()).orElse(null));
		faq.setPreguntaRealizadaPor(userRepository.findById(createFaqDTO.getPreguntaRealizadaPor()).orElse(null));
		faq.setRespuestaRealizadaPor(userRepository.findById(createFaqDTO.getRespuestaRealizadaPor()).orElse(null));

		if (faq.getCategoria() == null || faq.getPreguntaRealizadaPor() == null
				|| faq.getRespuestaRealizadaPor() == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Pregunta no encontrada");
		}

		try {
			faqRepository.save(faq);
			return ResponseEntity.status(HttpStatus.CREATED).body("Pregunta creada");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al crear la pregunta");
		}
	}

	/**
	 * Edits an existing FAQ with the provided FAQ code and updates its details
	 * based on the provided FaqEditDTO.
	 * 
	 * @param faqCode    The code of the FAQ to be edited.
	 * @param editFaqDTO The FaqEditDTO containing updated information for the FAQ.
	 * @return ResponseEntity<String> An HTTP response indicating the result of the
	 *         operation. Returns ResponseEntity with status OK and a message "FAQ
	 *         Actualizada" if the FAQ is successfully updated. Returns
	 *         ResponseEntity with status NOT_FOUND and a message "FAQ No
	 *         encontrada" if the FAQ with the provided code is not found.
	 */
	public ResponseEntity<String> editFaq(Integer faqCode, FaqEditDTO editFaqDTO) {
		Optional<Faq> existingFaqOptional = faqRepository.findById(faqCode);

		if (existingFaqOptional.isPresent()) {
			Faq existingFaq = existingFaqOptional.get();

			ModelMapper modelMapper = new ModelMapper();
			modelMapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());
			modelMapper.map(editFaqDTO, existingFaq);

			editFaqDTO.getCategoria().ifPresent(
					categoriaId -> faqCategoriesRepository.findById(categoriaId).ifPresent(existingFaq::setCategoria));

			editFaqDTO.getPreguntaRealizadaPor().ifPresent(
					preguntaId -> userRepository.findById(preguntaId).ifPresent(existingFaq::setPreguntaRealizadaPor));

			editFaqDTO.getRespuestaRealizadaPor().ifPresent(respuestaId -> userRepository.findById(respuestaId)
					.ifPresent(existingFaq::setRespuestaRealizadaPor));

			try {
				faqRepository.save(existingFaq);
				return ResponseEntity.ok("Pregunta actualizada");
			} catch (Exception e) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Pregunta no encontrada");
			}
		} else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al editar la pregunta");
		}
	}
}
