package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.dto.TaskDTO;
import org.example.entity.Task;
import org.example.entity.TaskStatus;
import org.example.entity.Team;
import org.example.exception.InvalidTaskStatusTransitionException;
import org.example.repository.TeamRepository;
import org.example.service.TaskService;
import org.example.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
@Tag(name = "Task Controller", description = "API для управления задачами")
@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;
    @Autowired
    private TeamService teamService;

    // Метод для создания задачи
    @Operation(summary = "Создать задачу")
    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody TaskDTO taskDTO) {
        try {
            // Преобразуем DTO в сущность Task
            Task task = new Task();
            task.setDescription(taskDTO.getDescription());
            task.setStatus(TaskStatus.valueOf(taskDTO.getStatus()));

            // Проверяем наличие teamId и, если оно присутствует, находим команду
            if (taskDTO.getTeamId() != null) {
                Team team = teamService.getTeamByIdWithTasks(taskDTO.getTeamId());
                task.setTeam(team);
            }

            task.setCreatedAt(LocalDateTime.now());
            Task createdTask = taskService.createTask(task);

            return ResponseEntity.ok(createdTask);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }


    // Метод для обновления статуса задачи
    @Operation(summary = "Обновить статус задачи")
    @PatchMapping("/{id}/status")
    public ResponseEntity<Task> updateTaskStatus(@PathVariable Long id, @RequestParam TaskStatus status) {
        return ResponseEntity.ok(taskService.updateTaskStatus(id, status));
    }

    // Метод для присвоения команды задаче
    @Operation(summary = "Присвоить задаче команду")
    @PatchMapping("/{id}/team")
    public ResponseEntity<Task> assignTeamToTask(@PathVariable Long id, @RequestParam Long teamId) {
        return ResponseEntity.ok(taskService.assignTeamToTask(id, teamId));
    }


    // Метод для получения всех задач
    @Operation(summary = "Получить все задачи")
    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks() {
        return ResponseEntity.ok(taskService.getAllTasks());
    }

    // Метод для получения задачи по её идентификатору
    @Operation(summary = "Получить задачу по ее ID")
    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.getTaskById(id));
    }

    // Метод для получения задач команды по ID
    @Operation(summary = "Получить задачи команд по ID")
    @GetMapping("/team/{teamId}")
    public ResponseEntity<List<Task>> getTasksByTeamId(@PathVariable Long teamId) {
        return ResponseEntity.ok(taskService.getTasksByTeamId(teamId));
    }

    @ExceptionHandler(InvalidTaskStatusTransitionException.class)
    public ResponseEntity<String> handleInvalidTaskStatusTransition(InvalidTaskStatusTransitionException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}
