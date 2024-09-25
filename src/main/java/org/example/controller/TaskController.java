package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import org.example.dto.TaskDTO;
import org.example.entity.TaskStatus;
import org.example.exception.InvalidTaskStatusTransitionException;
import org.example.exception.ResourceNotFoundException;
import org.example.service.TaskService;
import org.example.service.TeamService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Data
@Tag(name = "Task Controller", description = "API для управления задачами")
@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);

    private final TaskService taskService;
    private final TeamService teamService;

    public TaskController(TaskService taskService, TeamService teamService) {
        this.taskService = taskService;
        this.teamService = teamService;
    }

    @Operation(summary = "Создать задачу")
    @PostMapping
    public ResponseEntity<TaskDTO> createTask(@RequestBody TaskDTO taskDTO) {
        try {
            TaskDTO createdTask = taskService.createTaskFromDTO(taskDTO);
            return ResponseEntity.ok(createdTask);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid input: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (NoSuchElementException e) {
            logger.error("Team not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            logger.error("Unexpected error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @Operation(summary = "Обновить статус задачи")
    @PatchMapping("/{id}/status")
    public ResponseEntity<TaskDTO> updateTaskStatus(@PathVariable Long id, @RequestParam String status) {
        try {
            TaskStatus taskStatus = TaskStatus.valueOf(status); // Преобразуем строку в TaskStatus
            TaskDTO updatedTask = taskService.updateTaskStatus(id, taskStatus);
            return ResponseEntity.ok(updatedTask);
        } catch (InvalidTaskStatusTransitionException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @Operation(summary = "Присвоить задаче команду")
    @PatchMapping("/{id}/team")
    public ResponseEntity<TaskDTO> assignTeamToTask(@PathVariable Long id, @RequestParam Long teamId) {
        try {
            TaskDTO updatedTask = taskService.assignTeamToTask(id, teamId);
            return ResponseEntity.ok(updatedTask);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @Operation(summary = "Получить все задачи")
    @GetMapping
    public ResponseEntity<List<TaskDTO>> getAllTasks() {
        List<TaskDTO> taskDTOs = taskService.getAllTasks();
        return ResponseEntity.ok(taskDTOs);
    }

    @Operation(summary = "Получить задачу по ее ID")
    @GetMapping("/{id}")
    public ResponseEntity<TaskDTO> getTaskById(@PathVariable Long id) {
        try {
            TaskDTO task = taskService.getTaskById(id);
            return ResponseEntity.ok(task);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @Operation(summary = "Получить задачи команд по ID")
    @GetMapping("/team/{teamId}")
    public ResponseEntity<List<TaskDTO>> getTasksByTeamId(@PathVariable Long teamId) {
        try {
            List<TaskDTO> taskDTOs = taskService.getTasksByTeamId(teamId);
            return ResponseEntity.ok(taskDTOs);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @ExceptionHandler(InvalidTaskStatusTransitionException.class)
    public ResponseEntity<String> handleInvalidTaskStatusTransition(InvalidTaskStatusTransitionException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}
