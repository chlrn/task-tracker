package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.dto.TaskDTO;
import org.example.entity.TaskStatus;
import org.example.exception.InvalidTaskStatusTransitionException;
import org.example.exception.ResourceNotFoundException;
import org.example.service.TaskService;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Task Controller", description = "API для управления задачами")
@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @Operation(summary = "Создать задачу")
    @PostMapping
    public ResponseEntity<TaskDTO> createTask(@RequestBody TaskDTO taskDTO) {
        TaskDTO createdTask = taskService.createTaskFromDTO(taskDTO);
        return ResponseEntity.ok(createdTask);
    }

    @Operation(summary = "Обновить статус задачи")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @PatchMapping("/{id}/status")
    public ResponseEntity<TaskDTO> updateTaskStatus(@PathVariable Long id, @RequestParam String  status) { // Изменено: используем TaskStatus
        return taskService.updateTaskStatus(id, status);
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
