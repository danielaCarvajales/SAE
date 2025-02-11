package com.siscem.portal_sae.services;

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

import com.siscem.portal_sae.dtos.activity.ActivityCreateDTO;
import com.siscem.portal_sae.dtos.activity.ActivityDTO;
import com.siscem.portal_sae.dtos.activity.ActivityEditDTO;
import com.siscem.portal_sae.models.Activity;
import com.siscem.portal_sae.models.Contact;
import com.siscem.portal_sae.models.Task;
import com.siscem.portal_sae.models.relationships.ActivityStatus;
import com.siscem.portal_sae.models.relationships.WorkUnitsActivity;
import com.siscem.portal_sae.repositories.ActivityRepository;
import com.siscem.portal_sae.repositories.ContactRepository;
import com.siscem.portal_sae.repositories.TaskRepository;
import com.siscem.portal_sae.repositories.Relationships.ActivityStatusRepository;
import com.siscem.portal_sae.repositories.Relationships.WorkUnitsActivityRepository;

@Service
public class ActivityService {

	@Autowired
	ActivityRepository activityRepository;

	@Autowired
	TaskRepository taskRepository;

	@Autowired
	ContactRepository contactRepository;

	@Autowired
	ActivityStatusRepository activityStatusRepository;

	@Autowired
	WorkUnitsActivityRepository workUnitsActivityRepository;

	/**
	 * Retrieves activities associated with a given task code and maps them to DTOs.
	 * 
	 * @param taskCode The code of the task for which activities are retrieved.
	 * @return A list of ActivityDTO objects.
	 */
	public ResponseEntity<List<ActivityDTO>> getActivitiesForTask(Integer taskCode) {
		List<Activity> activities = activityRepository.findByCodigoTareaCodigo(taskCode);

		ModelMapper modelMapper = new ModelMapper();
		TypeMap<Activity, ActivityDTO> typeMap = modelMapper.createTypeMap(Activity.class, ActivityDTO.class);
		typeMap.addMappings(mp -> {
			mp.skip(ActivityDTO::setCodigoTarea);
			mp.skip(ActivityDTO::setContactoAsignado);
			mp.skip(ActivityDTO::setEstado);
			mp.skip(ActivityDTO::setUnidadTrabajo);
		});

		return ResponseEntity.ok(activities.stream().map(actividad -> {
			ActivityDTO actividadDTO = modelMapper.map(actividad, ActivityDTO.class);
			actividadDTO.setCodigoTarea(actividad.getCodigoTarea().getAsunto());
			actividadDTO.setContactoAsignado(actividad.getContactoAsignado().getNombres()
					.concat(" " + actividad.getContactoAsignado().getApellidos()));
			actividadDTO.setEstado(actividad.getEstado().getNombreEstado());
			actividadDTO.setUnidadTrabajo(actividad.getUnidadTrabajo().getNombreUnidad());
			return actividadDTO;
		}).collect(Collectors.toList()));
	}

	/**
	 * Creates a new activity based on the provided ActivityCreateDTO.
	 * 
	 * @param createActivityDTO The DTO containing information for creating the
	 *                          activity.
	 * @return A response entity indicating the success or failure of the operation.
	 */
	public ResponseEntity<String> createActivity(ActivityCreateDTO createActivityDTO) {
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.addMappings(new PropertyMap<ActivityCreateDTO, Activity>() {
			@Override
			protected void configure() {
				skip(destination.getCodigo());
			}
		});

		Activity activity = modelMapper.map(createActivityDTO, Activity.class);
		activity.setCodigoTarea(taskRepository.findById(createActivityDTO.getCodigoTarea()).orElse(null));
		activity.setContactoAsignado(contactRepository.findById(createActivityDTO.getContactoAsignado()).orElse(null));
		activity.setEstado(activityStatusRepository.findById(createActivityDTO.getEstado()).orElse(null));
		activity.setUnidadTrabajo(
				workUnitsActivityRepository.findById(createActivityDTO.getUnidadTrabajo()).orElse(null));

		if (activity.getCodigoTarea() == null || activity.getContactoAsignado() == null || activity.getEstado() == null
				|| activity.getUnidadTrabajo() == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Actividad no encontrada");
		}

		try {
			activityRepository.save(activity);
			return ResponseEntity.ok("Actividad creada");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al crear la actividad");
		}
	}

	/**
	 * Edits an existing activity based on the provided ActivityEditDTO.
	 * 
	 * @param activityCode    The code of the activity to be edited.
	 * @param editActivityDTO The DTO containing information for editing the
	 *                        activity.
	 * @return A response entity indicating the success or failure of the operation.
	 */
	public ResponseEntity<String> editActivity(Integer activityCode, ActivityEditDTO editActivityDTO) {
		Optional<Activity> existingActivityOptional = activityRepository.findById(activityCode);

		if (existingActivityOptional.isPresent()) {
			Activity existingActivity = existingActivityOptional.get();

			ModelMapper modelMapper = new ModelMapper();
			modelMapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());
			modelMapper.addMappings(new PropertyMap<ActivityEditDTO, Activity>() {
				@Override
				protected void configure() {
					skip(destination.getCodigo());
				}
			});

			modelMapper.map(editActivityDTO, existingActivity);

			editActivityDTO.getCodigoTarea().ifPresent(codigoTarea -> {
				Task tarea = taskRepository.findById(codigoTarea).orElse(null);
				existingActivity.setCodigoTarea(tarea);
			});

			editActivityDTO.getContactoAsignado().ifPresent(contactoAsignado -> {
				Contact contacto = contactRepository.findById(contactoAsignado).orElse(null);
				existingActivity.setContactoAsignado(contacto);
			});

			editActivityDTO.getEstado().ifPresent(estado -> {
				ActivityStatus activityStatus = activityStatusRepository.findById(estado).orElse(null);
				existingActivity.setEstado(activityStatus);
			});

			editActivityDTO.getUnidadTrabajo().ifPresent(unidadTrabajo -> {
				WorkUnitsActivity workUnitsActivity = workUnitsActivityRepository.findById(unidadTrabajo).orElse(null);
				existingActivity.setUnidadTrabajo(workUnitsActivity);
			});

			try {
				activityRepository.save(existingActivity);
				return ResponseEntity.ok("Actividad actualizada");
			} catch (Exception e) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar la actividad");
			}
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Actividad no encontrada");
		}
	}

	/**
	 * Deletes an activity with the given activity code.
	 * 
	 * @param activityCode The code of the activity to be deleted.
	 * @return A response entity indicating the success or failure of the operation.
	 */
	public ResponseEntity<String> deleteActivity(Integer activityCode) {
		try {
			if (activityRepository.existsById(activityCode)) {
				activityRepository.deleteById(activityCode);
				return ResponseEntity.ok("Actividad eliminada");
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Actividad no encontrada");
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al eliminar la actividad");
		}
	}

	public ResponseEntity<List<ActivityDTO>> getAllActivities() {
		List<Activity> activities = activityRepository.findAll();

		ModelMapper modelMapper = new ModelMapper();
		TypeMap<Activity, ActivityDTO> typeMap = modelMapper.createTypeMap(Activity.class, ActivityDTO.class);
		typeMap.addMappings(mp -> {
			mp.skip(ActivityDTO::setCodigoTarea);
			mp.skip(ActivityDTO::setContactoAsignado);
			mp.skip(ActivityDTO::setEstado);
			mp.skip(ActivityDTO::setUnidadTrabajo);
		});

		List<ActivityDTO> activityDTOs = activities.stream().map(actividad -> {
			ActivityDTO actividadDTO = modelMapper.map(actividad, ActivityDTO.class);
			actividadDTO.setCodigoTarea(actividad.getCodigoTarea().getAsunto());
			actividadDTO.setContactoAsignado(actividad.getContactoAsignado().getNombres()
					.concat(" " + actividad.getContactoAsignado().getApellidos()));
			actividadDTO.setEstado(actividad.getEstado().getNombreEstado());
			actividadDTO.setUnidadTrabajo(actividad.getUnidadTrabajo().getNombreUnidad());
			return actividadDTO;
		}).collect(Collectors.toList());

		return ResponseEntity.ok(activityDTOs);
	}

}
