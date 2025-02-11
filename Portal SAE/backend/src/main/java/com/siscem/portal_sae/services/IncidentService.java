package com.siscem.portal_sae.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.TypeMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.siscem.portal_sae.dtos.incident.IncidentCreateDTO;
import com.siscem.portal_sae.dtos.incident.IncidentDTO;
import com.siscem.portal_sae.dtos.incident.IncidentEditDTO;
import com.siscem.portal_sae.models.Incident;
import com.siscem.portal_sae.repositories.IncidentRepository;
import com.siscem.portal_sae.repositories.UserRepository;
import com.siscem.portal_sae.repositories.Relationships.IncidentCategoriesRepository;
import com.siscem.portal_sae.repositories.Relationships.IncidentStatusRepository;

@Service
public class IncidentService {

	@Autowired
	IncidentRepository incidentRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	IncidentCategoriesRepository incidentCategoriesRepository;

	@Autowired
	IncidentStatusRepository incidentStatusRepository;

	/**
	 * Retrieves incidents associated with a user based on their role (consultor or
	 * empresario) and converts them into IncidentDTO objects.
	 * 
	 * @param userCode The code of the user for whom incidents are to be retrieved.
	 * @param role     The role of the user (consultor or empresario).
	 * @return List<IncidentDTO> A list of IncidentDTO objects representing
	 *         incidents associated with the user.
	 */
	public ResponseEntity<List<IncidentDTO>> getIncidentsForUser(Integer userCode, String role) {
		List<Incident> incidents = switch (role.toLowerCase()) {
		case "consultor" -> incidentRepository.findByCodigoConsultorCodigo(userCode);
		case "empresario" -> incidentRepository.findByCodigoEmpresarioCodigo(userCode);
		default -> new ArrayList<>();
		};

		ModelMapper modelMapper = new ModelMapper();
		TypeMap<Incident, IncidentDTO> typeMap = modelMapper.createTypeMap(Incident.class, IncidentDTO.class);
		typeMap.addMappings(mp -> {
			mp.skip(IncidentDTO::setCodigoConsultor);
			mp.skip(IncidentDTO::setCodigoEmpresario);
			mp.skip(IncidentDTO::setCategoria);
			mp.skip(IncidentDTO::setEstado);
		});

		return ResponseEntity.ok(incidents.stream().map(incident -> {
			IncidentDTO incidentDTO = modelMapper.map(incident, IncidentDTO.class);
			incidentDTO.setCodigoConsultor(incident.getCodigoConsultor().getNombre());
			incidentDTO.setCodigoEmpresario(incident.getCodigoEmpresario().getNombre());
			incidentDTO.setCategoria(incident.getCategoria().getNombreCategoria());
			incidentDTO.setEstado(incident.getEstado().getNombreEstado());
			return incidentDTO;
		}).collect(Collectors.toList()));
	}

	/**
	 * Creates a new incident based on the provided IncidentCreateDTO.
	 * 
	 * @param createIncidentDTO The IncidentCreateDTO containing information for
	 *                          creating the incident.
	 * @return ResponseEntity<String> An HTTP response indicating the result of the
	 *         operation. Returns ResponseEntity with status CREATED and a message
	 *         "Incidencia Creada" if the incident is successfully created.
	 */
	public ResponseEntity<String> createIncident(IncidentCreateDTO createIncidentDTO) {
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.addMappings(new PropertyMap<IncidentCreateDTO, Incident>() {
			@Override
			protected void configure() {
				skip(destination.getCodigo());
			}
		});

		Incident incident = modelMapper.map(createIncidentDTO, Incident.class);
		incident.setCodigoConsultor(userRepository.findById(createIncidentDTO.getCodigoConsultor()).orElse(null));
		incident.setCodigoEmpresario(userRepository.findById(createIncidentDTO.getCodigoEmpresario()).orElse(null));
		incident.setCategoria(incidentCategoriesRepository.findById(createIncidentDTO.getCategoria()).orElse(null));
		incident.setEstado(incidentStatusRepository.findById(createIncidentDTO.getEstado()).orElse(null));

		if (incident.getCodigoConsultor() == null || incident.getCodigoEmpresario() == null
				|| incident.getCategoria() == null || incident.getEstado() == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Incidencia no encontrada");
		}

		try {
			incidentRepository.save(incident);
			return ResponseEntity.status(HttpStatus.CREATED).body("Incidencia creada");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al crear la incidencia");
		}
	}

	/**
	 * Edits an existing incident with the provided incident code and updates its
	 * details based on the provided IncidentEditDTO.
	 * 
	 * @param incidentCode    The code of the incident to be edited.
	 * @param editIncidentDTO The IncidentEditDTO containing updated information for
	 *                        the incident.
	 * @return ResponseEntity<String> An HTTP response indicating the result of the
	 *         operation. Returns ResponseEntity with status OK and a message
	 *         "Incidencia Actualizada" if the incident is successfully updated.
	 *         Returns ResponseEntity with status NOT_FOUND and a message
	 *         "Incidencia No Encontrada" if the incident with the provided code is
	 *         not found.
	 */
	public ResponseEntity<String> editIncident(Integer incidentCode, IncidentEditDTO editIncidentDTO) {
		Optional<Incident> existingIncidentOptional = incidentRepository.findById(incidentCode);

		if (existingIncidentOptional.isPresent()) {
			Incident existingIncident = existingIncidentOptional.get();

			ModelMapper modelMapper = new ModelMapper();
			modelMapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());
			modelMapper.addMappings(new PropertyMap<IncidentEditDTO, Incident>() {
				@Override
				protected void configure() {
					skip(destination.getCodigo());
				}
			});

			modelMapper.map(editIncidentDTO, existingIncident);

			editIncidentDTO.getCodigoConsultor().ifPresent(
					codigo -> existingIncident.setCodigoConsultor(userRepository.findById(codigo).orElse(null)));
			editIncidentDTO.getCodigoEmpresario().ifPresent(
					codigo -> existingIncident.setCodigoEmpresario(userRepository.findById(codigo).orElse(null)));
			editIncidentDTO.getCategoria().ifPresent(codigo -> existingIncident
					.setCategoria(incidentCategoriesRepository.findById(codigo).orElse(null)));
			editIncidentDTO.getEstado().ifPresent(
					codigo -> existingIncident.setEstado(incidentStatusRepository.findById(codigo).orElse(null)));

			try {
				incidentRepository.save(existingIncident);
				return ResponseEntity.ok("Incidencia actualizada");
			} catch (Exception e) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Incidencia no encontrada");
			}
		} else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al editar la incidencia");
		}
	}
}
