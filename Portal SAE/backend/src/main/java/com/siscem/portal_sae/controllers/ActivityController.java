package com.siscem.portal_sae.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.siscem.portal_sae.dtos.activity.ActivityCreateDTO;
import com.siscem.portal_sae.dtos.activity.ActivityDTO;
import com.siscem.portal_sae.dtos.activity.ActivityEditDTO;
import com.siscem.portal_sae.services.ActivityService;
import com.siscem.portal_sae.services.JwtService;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/actividad")
public class ActivityController {

	@Autowired
	ActivityService activityService;

	@Autowired
	private JwtService jwtService;

	@Operation(summary = "Returns a list of activities given the task's code.")
	@GetMapping("/tarea/{taskCode}")
	public ResponseEntity<List<ActivityDTO>> getActivitiesForTask(
			@RequestHeader("Authorization") String authorizationHeader, @PathVariable Integer taskCode) {
		if (jwtService.validateToken(authorizationHeader)) {
			return activityService.getActivitiesForTask(taskCode);
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}

	@GetMapping("/listar")
	public ResponseEntity<List<ActivityDTO>> getAllActivities() {
		return activityService.getAllActivities();
	}


	@Operation(summary = "Creates an activity within the database.")
	@PostMapping
	public ResponseEntity<String> createActivity(@RequestHeader("Authorization") String authorizationHeader,
			@RequestBody ActivityCreateDTO activity) {
		if (jwtService.validateToken(authorizationHeader)) {
			return activityService.createActivity(activity);
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}

	@Operation(summary = "Updates an activity within the database.")
	@PutMapping("/{activityCode}")
	public ResponseEntity<String> editActivity(@RequestHeader("Authorization") String authorizationHeader,
			@PathVariable Integer activityCode, @RequestBody ActivityEditDTO activity) {
		if (jwtService.validateToken(authorizationHeader)) {
			return activityService.editActivity(activityCode, activity);
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}

	@Operation(summary = "Deletes an activity within the database.")
	@DeleteMapping("/{activityCode}")
	public ResponseEntity<String> deleteActivity(@RequestHeader("Authorization") String authorizationHeader,
			@PathVariable Integer activityCode) {
		if (jwtService.validateToken(authorizationHeader)) {
			return activityService.deleteActivity(activityCode);
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}

}
