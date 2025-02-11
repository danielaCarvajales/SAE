package com.siscem.portal_sae.services;

import java.util.Collections;
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

import com.siscem.portal_sae.dtos.task.TaskCreateDTO;
import com.siscem.portal_sae.dtos.task.TaskDTO;
import com.siscem.portal_sae.dtos.task.TaskEditDTO;
import com.siscem.portal_sae.models.Account;
import com.siscem.portal_sae.models.Task;
import com.siscem.portal_sae.models.User;
import com.siscem.portal_sae.models.relationships.TaskStatus;
import com.siscem.portal_sae.models.relationships.TaskTypes;
import com.siscem.portal_sae.repositories.AccountRepository;
import com.siscem.portal_sae.repositories.TaskRepository;
import com.siscem.portal_sae.repositories.UserRepository;
import com.siscem.portal_sae.repositories.Relationships.TaskStatusRepository;
import com.siscem.portal_sae.repositories.Relationships.TaskTypesRepository;

@Service
public class TaskService {

	@Autowired
	TaskRepository taskRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	AccountRepository accountRepository;

	@Autowired
	TaskStatusRepository taskStatusRepository;

	@Autowired
	TaskTypesRepository taskTypesRepository;

	/**
	 * Retrieves tasks associated with a user based on their role (consultor or
	 * empresario) and converts them into TaskDTO objects.
	 * 
	 * @param userCode The code of the user for whom tasks are to be retrieved.
	 * @param role     The role of the user (consultor or empresario).
	 * @return List<TaskDTO> A list of TaskDTO objects representing tasks associated
	 *         with the user.
	 */
	public ResponseEntity<List<TaskDTO>> getTasksForUser(Integer userCode, String role) {
		List<Task> tasks;

		switch (role.toLowerCase()) {
		case "consultor" -> tasks = taskRepository.findByConsultorAsignadoCodigo(userCode);
		case "empresario" -> tasks = taskRepository.findByEmpresarioAsignadoCodigo(userCode);
		default -> {
			return ResponseEntity.ok(Collections.emptyList());
		}
		}

		ModelMapper modelMapper = new ModelMapper();
		TypeMap<Task, TaskDTO> typeMap = modelMapper.createTypeMap(Task.class, TaskDTO.class);
		typeMap.addMappings(mp -> {
			mp.skip(TaskDTO::setConsultorAsignado);
			mp.skip(TaskDTO::setEmpresarioAsignado);
			mp.skip(TaskDTO::setCuentaAsignada);
			mp.skip(TaskDTO::setEstado);
			mp.skip(TaskDTO::setTipo);
		});

		return ResponseEntity.ok(tasks.stream().map(task -> {
			TaskDTO taskDTO = modelMapper.map(task, TaskDTO.class);
			taskDTO.setConsultorAsignado(task.getConsultorAsignado().getNombre());
			taskDTO.setEmpresarioAsignado(task.getEmpresarioAsignado().getNombre());
			taskDTO.setCuentaAsignada(task.getCuentaAsignada().getPaginaWeb());
			taskDTO.setEstado(task.getEstado().getNombreEstado());
			taskDTO.setTipo(task.getTipo().getNombreTipo());
			return taskDTO;
		}).collect(Collectors.toList()));
	}

	/**
	 * Creates a new task based on the provided TaskCreateDTO.
	 * 
	 * @param createTaskDTO The TaskCreateDTO containing information for creating
	 *                      the task.
	 * @return ResponseEntity<String> An HTTP response indicating the result of the
	 *         operation. Returns ResponseEntity with status CREATED and a message
	 *         "Tarea Creada" if the task is successfully created.
	 */
	public ResponseEntity<String> createTask(TaskCreateDTO createTaskDTO) {
		ModelMapper modelMapper = new ModelMapper();
		Task task = modelMapper.map(createTaskDTO, Task.class);
		task.setConsultorAsignado(userRepository.findById(createTaskDTO.getConsultorAsignado()).orElse(null));
		task.setEmpresarioAsignado(userRepository.findById(createTaskDTO.getEmpresarioAsignado()).orElse(null));
		task.setCuentaAsignada(accountRepository.findById(createTaskDTO.getCuentaAsignada()).orElse(null));
		task.setEstado(taskStatusRepository.findById(createTaskDTO.getEstado()).orElse(null));
		task.setTipo(taskTypesRepository.findById(createTaskDTO.getTipo()).orElse(null));

		if (task.getConsultorAsignado() == null || task.getEmpresarioAsignado() == null
				|| task.getCuentaAsignada() == null || task.getEstado() == null || task.getTipo() == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Tarea no encontrada");
		}

		try {
			taskRepository.save(task);
			return ResponseEntity.status(HttpStatus.CREATED).body("Tarea creada");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al crear la tarea");
		}
	}

	/**
	 * Edits an existing task with the provided task code and updates its details
	 * based on the provided TaskEditDTO.
	 * 
	 * @param taskCode    The code of the task to be edited.
	 * @param editTaskDTO The TaskEditDTO containing updated information for the
	 *                    task.
	 * @return ResponseEntity<String> An HTTP response indicating the result of the
	 *         operation. Returns ResponseEntity with status OK and a message "Tarea
	 *         Actualizada" if the task is successfully updated. Returns
	 *         ResponseEntity with status NOT_FOUND and a message "Tarea No
	 *         Encontrada" if the task with the provided code is not found.
	 */
	public ResponseEntity<String> editTask(Integer taskCode, TaskEditDTO editTaskDTO) {
		Optional<Task> existingTaskOptional = taskRepository.findById(taskCode);

		if (existingTaskOptional.isPresent()) {
			Task existingTask = existingTaskOptional.get();

			ModelMapper modelMapper = new ModelMapper();
			modelMapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());
			modelMapper.map(editTaskDTO, existingTask);

			editTaskDTO.getConsultorAsignado().ifPresent(consultorId -> {
				User consultant = userRepository.findById(consultorId).orElse(null);
				existingTask.setConsultorAsignado(consultant);
			});

			editTaskDTO.getEmpresarioAsignado().ifPresent(empresarioId -> {
				User businessman = userRepository.findById(empresarioId).orElse(null);
				existingTask.setEmpresarioAsignado(businessman);
			});

			editTaskDTO.getCuentaAsignada().ifPresent(cuentaId -> {
				Account assignedAccount = accountRepository.findById(cuentaId).orElse(null);
				existingTask.setCuentaAsignada(assignedAccount);
			});

			editTaskDTO.getEstado().ifPresent(estadoId -> {
				TaskStatus taskStatus = taskStatusRepository.findById(estadoId).orElse(null);
				existingTask.setEstado(taskStatus);
			});

			editTaskDTO.getTipo().ifPresent(tipoId -> {
				TaskTypes taskType = taskTypesRepository.findById(tipoId).orElse(null);
				existingTask.setTipo(taskType);
			});

			try {
				taskRepository.save(existingTask);
				return ResponseEntity.ok("Tarea actualizada");
			} catch (Exception e) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar la tarea");
			}
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Tarea no encontrada");
		}
	}

	/**
	 * Deletes an existing task with the provided task code.
	 * 
	 * @param taskCode The code of the task to be deleted.
	 * @return ResponseEntity<String> An HTTP response indicating the result of the
	 *         operation. Returns ResponseEntity with status OK and a message "Tarea
	 *         Eliminada" if the task is successfully deleted. Returns
	 *         ResponseEntity with status NOT_FOUND and a message "Tarea No
	 *         Encontrada" if the task with the provided code is not found.
	 */
	public ResponseEntity<String> deleteTask(Integer taskCode) {
		try {
			if (taskRepository.existsById(taskCode)) {
				taskRepository.deleteById(taskCode);
				return ResponseEntity.ok("Tarea eliminada");
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Tarea no encontrada");
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al eliminar la tarea");
		}
	}

}
