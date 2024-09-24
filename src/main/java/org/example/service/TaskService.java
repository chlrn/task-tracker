package org.example.service;

import org.example.entity.Task;
import org.example.entity.TaskStatus;
import org.example.entity.Team;
import org.example.exception.InvalidTaskStatusTransitionException;
import org.example.exception.ResourceNotFoundException;
import org.example.repository.TaskRepository;
import org.example.repository.TeamRepository;
import org.example.statemachine.TaskStateMachine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TeamRepository teamRepository;


    @Autowired
    private TaskStateMachine taskStateMachine;

    // Метод для создания задачи
    public Task createTask(Task task) {
        try {
            // Проверка на null для обязательных полей
            if (task == null) {
                throw new IllegalArgumentException("Task must not be null");
            }

            // Установите статус по умолчанию, если он не установлен
            if (task.getStatus() == null) {
                task.setStatus(TaskStatus.TODO);
            }


            // Установите дату создания
            task.setCreatedAt(LocalDateTime.now());

            return taskRepository.save(task);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error creating task", e);
        }
    }



    // Метод для обновления статуса задачи
    public Task updateTaskStatus(Long taskId, TaskStatus newStatus) {
        Optional<Task> taskOpt = taskRepository.findById(taskId);
        if (taskOpt.isPresent()) {
            Task task = taskOpt.get();
            if (taskStateMachine.canTransition(task.getStatus(), newStatus, task.getTeam().getWorkflow())) {
                task.setStatus(newStatus);
                task.setUpdatedAt(LocalDateTime.now());
                return taskRepository.save(task);
            } else {
                throw new InvalidTaskStatusTransitionException("Вы неправильно изменяете статус задачи");
            }
        }
        throw new RuntimeException("Task not found");
    }

    // Метод для присвоения команды по ID задачи

    public Task assignTeamToTask(Long taskId, Long teamId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found"));

        task.setTeam(team);
        task.setUpdatedAt(LocalDateTime.now());

        return taskRepository.save(task);
    }


    // Метод для получения всех задач
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    // Метод для получения задачи по её идентификатору
    public Task getTaskById(Long taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
    }

    // Метод для получения всех задач по ID команды
    public List<Task> getTasksByTeamId(Long teamId) {
        Optional<Team> teamOpt = teamRepository.findById(teamId);
        if (teamOpt.isPresent()) {
            Team team = teamOpt.get();
            return team.getTasks();
        }
        throw new RuntimeException("Team not found");
    }
}
