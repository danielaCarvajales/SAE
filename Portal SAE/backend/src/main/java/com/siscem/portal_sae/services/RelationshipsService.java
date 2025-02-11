package com.siscem.portal_sae.services;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.siscem.portal_sae.dtos.RelationshipsDTO;
import com.siscem.portal_sae.models.relationships.AccountServiceTypes;
import com.siscem.portal_sae.models.relationships.ActivityStatus;
import com.siscem.portal_sae.models.relationships.EventNotificationMethods;
import com.siscem.portal_sae.models.relationships.EventStatus;
import com.siscem.portal_sae.models.relationships.EventTypes;
import com.siscem.portal_sae.models.relationships.EventVisibilities;
import com.siscem.portal_sae.models.relationships.FaqCategories;
import com.siscem.portal_sae.models.relationships.IdentificationTypes;
import com.siscem.portal_sae.models.relationships.IncidentCategories;
import com.siscem.portal_sae.models.relationships.IncidentStatus;
import com.siscem.portal_sae.models.relationships.TaskStatus;
import com.siscem.portal_sae.models.relationships.TaskTypes;
import com.siscem.portal_sae.models.relationships.UserRoles;
import com.siscem.portal_sae.models.relationships.WorkUnitsActivity;
import com.siscem.portal_sae.repositories.Relationships.AccountServiceTypesRepository;
import com.siscem.portal_sae.repositories.Relationships.ActivityStatusRepository;
import com.siscem.portal_sae.repositories.Relationships.EventNotificationMethodsRepository;
import com.siscem.portal_sae.repositories.Relationships.EventStatusRepository;
import com.siscem.portal_sae.repositories.Relationships.EventTypesRepository;
import com.siscem.portal_sae.repositories.Relationships.EventVisibilitiesRepository;
import com.siscem.portal_sae.repositories.Relationships.FaqCategoriesRepository;
import com.siscem.portal_sae.repositories.Relationships.IdentificationTypesRepository;
import com.siscem.portal_sae.repositories.Relationships.IncidentCategoriesRepository;
import com.siscem.portal_sae.repositories.Relationships.IncidentStatusRepository;
import com.siscem.portal_sae.repositories.Relationships.TaskStatusRepository;
import com.siscem.portal_sae.repositories.Relationships.TaskTypesRepository;
import com.siscem.portal_sae.repositories.Relationships.UserRolesRepository;
import com.siscem.portal_sae.repositories.Relationships.WorkUnitsActivityRepository;

@Service
public class RelationshipsService {

	@Autowired
	EventVisibilitiesRepository eventVisibilitiesRepository;

	@Autowired
	WorkUnitsActivityRepository workUnitsActivityRepository;

	@Autowired
	TaskTypesRepository taskTypesRepository;

	@Autowired
	AccountServiceTypesRepository accountServiceTypesRepository;

	@Autowired
	IdentificationTypesRepository identificationTypesRepository;

	@Autowired
	EventTypesRepository eventTypesRepository;

	@Autowired
	UserRolesRepository userRolesRepository;

	@Autowired
	EventNotificationMethodsRepository eventNotificationMethodsRepository;

	@Autowired
	TaskStatusRepository taskStatusRepository;

	@Autowired
	IncidentStatusRepository incidentStatusRepository;

	@Autowired
	EventStatusRepository eventStatusRepository;

	@Autowired
	ActivityStatusRepository activityStatusRepository;

	@Autowired
	IncidentCategoriesRepository incidentCategoriesRepository;

	@Autowired
	FaqCategoriesRepository faqCategoriesRepository;

	@Autowired
	ModelMapper modelMapper;

	// Generic method to fetch relationships
	private <T> ResponseEntity<List<RelationshipsDTO>> getRelationships(List<T> entities) {
		return ResponseEntity.ok(entities.stream().map(entity -> modelMapper.map(entity, RelationshipsDTO.class))
				.collect(Collectors.toList()));
	}

	public ResponseEntity<List<RelationshipsDTO>> getEventVisibilities() {
		List<EventVisibilities> eventVisibilities = eventVisibilitiesRepository.findAll();
		return getRelationships(eventVisibilities);
	}

	public ResponseEntity<List<RelationshipsDTO>> getWorkUnitsActivity() {
		List<WorkUnitsActivity> workUnitsActivity = workUnitsActivityRepository.findAll();
		return getRelationships(workUnitsActivity);
	}

	public ResponseEntity<List<RelationshipsDTO>> getTaskTypes() {
		List<TaskTypes> taskTypes = taskTypesRepository.findAll();
		return getRelationships(taskTypes);
	}

	public ResponseEntity<List<RelationshipsDTO>> getAccountServiceTypes() {
		List<AccountServiceTypes> accountServiceTypes = accountServiceTypesRepository.findAll();
		return getRelationships(accountServiceTypes);
	}

	public ResponseEntity<List<RelationshipsDTO>> getIdentificationTypes() {
		List<IdentificationTypes> identificationTypes = identificationTypesRepository.findAll();
		return getRelationships(identificationTypes);
	}

	public ResponseEntity<List<RelationshipsDTO>> getEventTypes() {
		List<EventTypes> eventTypes = eventTypesRepository.findAll();
		return getRelationships(eventTypes);
	}

	public ResponseEntity<List<RelationshipsDTO>> getUserRoles() {
		List<UserRoles> userRoles = userRolesRepository.findAll();
		return getRelationships(userRoles);
	}

	public ResponseEntity<List<RelationshipsDTO>> getEventNotificationMethods() {
		List<EventNotificationMethods> eventNotificationMethods = eventNotificationMethodsRepository.findAll();
		return getRelationships(eventNotificationMethods);
	}

	public ResponseEntity<List<RelationshipsDTO>> getTaskStatus() {
		List<TaskStatus> taskStatus = taskStatusRepository.findAll();
		return getRelationships(taskStatus);
	}

	public ResponseEntity<List<RelationshipsDTO>> getIncidentStatus() {
		List<IncidentStatus> incidentStatus = incidentStatusRepository.findAll();
		return getRelationships(incidentStatus);
	}

	public ResponseEntity<List<RelationshipsDTO>> getEventStatus() {
		List<EventStatus> eventStatus = eventStatusRepository.findAll();
		return getRelationships(eventStatus);
	}

	public ResponseEntity<List<RelationshipsDTO>> getActivityStatus() {
		List<ActivityStatus> activityStatus = activityStatusRepository.findAll();
		return getRelationships(activityStatus);
	}

	public ResponseEntity<List<RelationshipsDTO>> getIncidentCategories() {
		List<IncidentCategories> incidentCategories = incidentCategoriesRepository.findAll();
		return getRelationships(incidentCategories);
	}

	public ResponseEntity<List<RelationshipsDTO>> getFaqCategories() {
		List<FaqCategories> faqCategories = faqCategoriesRepository.findAll();
		return getRelationships(faqCategories);
	}
}
