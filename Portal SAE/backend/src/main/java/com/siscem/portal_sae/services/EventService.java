package com.siscem.portal_sae.services;

import java.util.Collections;
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

import com.siscem.portal_sae.dtos.event.EventCreateDTO;
import com.siscem.portal_sae.dtos.event.EventDTO;
import com.siscem.portal_sae.dtos.event.EventEditDTO;
import com.siscem.portal_sae.models.Event;
import com.siscem.portal_sae.repositories.AccountRepository;
import com.siscem.portal_sae.repositories.ContactRepository;
import com.siscem.portal_sae.repositories.EventRepository;
import com.siscem.portal_sae.repositories.UserRepository;
import com.siscem.portal_sae.repositories.Relationships.EventNotificationMethodsRepository;
import com.siscem.portal_sae.repositories.Relationships.EventStatusRepository;
import com.siscem.portal_sae.repositories.Relationships.EventTypesRepository;
import com.siscem.portal_sae.repositories.Relationships.EventVisibilitiesRepository;

@Service
public class EventService {

	@Autowired
	EventRepository eventRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	EventStatusRepository eventStatusRepository;

	@Autowired
	EventTypesRepository eventTypesRepository;

	@Autowired
	EventVisibilitiesRepository eventVisibilitiesRepository;

	@Autowired
	EventNotificationMethodsRepository eventNotificationMethodsRepository;

	@Autowired
	AccountRepository accountRepository;

	@Autowired
	ContactRepository contactRepository;

	/**
	 * Retrieves events associated with a given user code and role, and maps them to
	 * DTOs.
	 * 
	 * @param userCode The code of the user for which events are retrieved.
	 * @param role     The role of the user (consultor or empresario).
	 * @return A list of EventDTO objects.
	 */
	public ResponseEntity<List<EventDTO>> getEventsForUser(Integer userCode, String role) {
		List<Event> events = switch (role.toLowerCase()) {
		case "consultor" -> eventRepository.findByCodigoConsultorCodigo(userCode);
		case "empresario" -> eventRepository.findByCodigoEmpresarioCodigo(userCode);
		default -> Collections.emptyList();
		};

		ModelMapper modelMapper = new ModelMapper();
		TypeMap<Event, EventDTO> typeMap = modelMapper.createTypeMap(Event.class, EventDTO.class);
		typeMap.addMappings(mp -> {
			mp.skip(EventDTO::setCodigoConsultor);
			mp.skip(EventDTO::setCodigoEmpresario);
			mp.skip(EventDTO::setEstado);
			mp.skip(EventDTO::setTipo);
			mp.skip(EventDTO::setVisibilidad);
			mp.skip(EventDTO::setNotificarPor);
			mp.skip(EventDTO::setCuentaAsignada);
			mp.skip(EventDTO::setContactoAsignado);
		});

		return ResponseEntity.ok(events.stream().map(evento -> {
			EventDTO eventoDTO = modelMapper.map(evento, EventDTO.class);
			eventoDTO.setCodigoConsultor(evento.getCodigoConsultor().getNombre());
			eventoDTO.setCodigoEmpresario(evento.getCodigoEmpresario().getNombre());
			eventoDTO.setEstado(evento.getEstado().getNombreEstado());
			eventoDTO.setTipo(evento.getTipo().getNombreTipo());
			eventoDTO.setVisibilidad(evento.getVisibilidad().getNombreVisibilidad());
			eventoDTO.setNotificarPor(evento.getNotificarPor().getNombreMedio());
			eventoDTO.setCuentaAsignada(evento.getCuentaAsignada().getPaginaWeb());
			eventoDTO.setContactoAsignado(evento.getContactoAsignado().getNombres()
					.concat(" " + evento.getContactoAsignado().getApellidos()));
			return eventoDTO;
		}).collect(Collectors.toList()));
	}

	/**
	 * Creates a new event based on the provided EventCreateDTO.
	 * 
	 * @param createEventDTO The DTO containing information for creating the event.
	 * @return A response entity indicating the success or failure of the operation.
	 */
	public ResponseEntity<String> createEvent(EventCreateDTO createEventDTO) {
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.addMappings(new PropertyMap<EventCreateDTO, Event>() {
			@Override
			protected void configure() {
				skip(destination.getCodigo());
			}
		});

		Event event = modelMapper.map(createEventDTO, Event.class);
		event.setCodigoConsultor(userRepository.findById(createEventDTO.getCodigoConsultor()).orElse(null));
		event.setCodigoEmpresario(userRepository.findById(createEventDTO.getCodigoEmpresario()).orElse(null));
		event.setEstado(eventStatusRepository.findById(createEventDTO.getEstado()).orElse(null));
		event.setTipo(eventTypesRepository.findById(createEventDTO.getTipo()).orElse(null));
		event.setVisibilidad(eventVisibilitiesRepository.findById(createEventDTO.getVisibilidad()).orElse(null));
		event.setNotificarPor(
				eventNotificationMethodsRepository.findById(createEventDTO.getNotificarPor()).orElse(null));
		event.setCuentaAsignada(accountRepository.findById(createEventDTO.getCuentaAsignada()).orElse(null));
		event.setContactoAsignado(contactRepository.findById(createEventDTO.getContactoAsignado()).orElse(null));

		if (event.getCodigoConsultor() == null || event.getCodigoEmpresario() == null || event.getEstado() == null
				|| event.getTipo() == null || event.getVisibilidad() == null || event.getNotificarPor() == null
				|| event.getCuentaAsignada() == null || event.getContactoAsignado() == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Evento no encontrado");
		}

		try {
			eventRepository.save(event);
			return ResponseEntity.ok("Evento creado");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al crear el evento");
		}
	}

	/**
	 * Edits an existing event based on the provided EventEditDTO.
	 * 
	 * @param eventCode    The code of the event to be edited.
	 * @param editEventDTO The DTO containing information for editing the event.
	 * @return A response entity indicating the success or failure of the operation.
	 */
	public ResponseEntity<String> editEvent(Integer eventCode, EventEditDTO editEventDTO) {
		Optional<Event> existingEventOptional = eventRepository.findById(eventCode);

		if (existingEventOptional.isPresent()) {
			Event existingEvent = existingEventOptional.get();

			ModelMapper modelMapper = new ModelMapper();
			modelMapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());
			modelMapper.addMappings(new PropertyMap<EventEditDTO, Event>() {
				@Override
				protected void configure() {
					skip(destination.getCodigo());
				}
			});

			modelMapper.map(editEventDTO, existingEvent);

			editEventDTO.getCodigoConsultor()
					.ifPresent(id -> existingEvent.setCodigoConsultor(userRepository.findById(id).orElse(null)));
			editEventDTO.getCodigoEmpresario()
					.ifPresent(id -> existingEvent.setCodigoEmpresario(userRepository.findById(id).orElse(null)));
			editEventDTO.getEstado()
					.ifPresent(id -> existingEvent.setEstado(eventStatusRepository.findById(id).orElse(null)));
			editEventDTO.getTipo()
					.ifPresent(id -> existingEvent.setTipo(eventTypesRepository.findById(id).orElse(null)));
			editEventDTO.getVisibilidad().ifPresent(
					id -> existingEvent.setVisibilidad(eventVisibilitiesRepository.findById(id).orElse(null)));
			editEventDTO.getNotificarPor().ifPresent(
					id -> existingEvent.setNotificarPor(eventNotificationMethodsRepository.findById(id).orElse(null)));
			editEventDTO.getCuentaAsignada()
					.ifPresent(id -> existingEvent.setCuentaAsignada(accountRepository.findById(id).orElse(null)));
			editEventDTO.getContactoAsignado()
					.ifPresent(id -> existingEvent.setContactoAsignado(contactRepository.findById(id).orElse(null)));

			try {
				eventRepository.save(existingEvent);
				return ResponseEntity.ok("Evento actualizado");
			} catch (Exception e) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar el evento");
			}
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Evento no encontrado");
		}
	}

	/**
	 * Deletes an event with the given event code.
	 * 
	 * @param eventCode The unique identifier of the event to be deleted.
	 * @return ResponseEntity<String> An HTTP response indicating the result of the
	 *         operation. If the event is successfully deleted, returns
	 *         ResponseEntity with status OK and a message "Evento Eliminado". If
	 *         the event with the given event code does not exist, returns
	 *         ResponseEntity with status NOT_FOUND and a message "Evento No
	 *         Encontrado".
	 */
	public ResponseEntity<String> deleteEvent(Integer eventCode) {
		try {
			if (eventRepository.existsById(eventCode)) {
				eventRepository.deleteById(eventCode);
				return ResponseEntity.ok("Evento eliminado");
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Evento no encontrado");
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al eliminar el evento");
		}
	}
}
