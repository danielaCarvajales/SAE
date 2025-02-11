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

import com.siscem.portal_sae.dtos.task.TaskCreateDTO;
import com.siscem.portal_sae.dtos.task.TaskDTO;
import com.siscem.portal_sae.dtos.task.TaskEditDTO;
import com.siscem.portal_sae.services.JwtService;
import com.siscem.portal_sae.services.TaskService;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/tarea")
public class TaskController {

	@Autowired
	TaskService taskService;

	@Autowired
	private JwtService jwtService;

	@Operation(summary = "Returns a list of tasks given the user's code and role.")
	@GetMapping("/usuario/{userCode}/{role}")
	public ResponseEntity<List<TaskDTO>> getTasksForUser(@RequestHeader("Authorization") String authorizationHeader,
			@PathVariable("userCode") Integer userCode, @PathVariable("role") String role) {
		if (jwtService.validateToken(authorizationHeader)) {
			return taskService.getTasksForUser(userCode, role);
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}

	@Operation(summary = "Creates a task within the database.")
	@PostMapping
	public ResponseEntity<String> createTask(@RequestHeader("Authorization") String authorizationHeader,
			@RequestBody TaskCreateDTO task) {
		if (jwtService.validateToken(authorizationHeader)) {
			return taskService.createTask(task);
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}

	@Operation(summary = "Updates a task within the database.")
	@PutMapping("/{taskCode}")
	public ResponseEntity<String> editTask(@RequestHeader("Authorization") String authorizationHeader,
			@PathVariable Integer taskCode, @RequestBody TaskEditDTO task) {
		if (jwtService.validateToken(authorizationHeader)) {
			return taskService.editTask(taskCode, task);
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}

	@Operation(summary = "Deletes a task within the database.")
	@DeleteMapping("/{taskCode}")
	public ResponseEntity<String> deleteTask(@RequestHeader("Authorization") String authorizationHeader,
			@PathVariable Integer taskCode) {
		if (jwtService.validateToken(authorizationHeader)) {
			return taskService.deleteTask(taskCode);
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}

}
