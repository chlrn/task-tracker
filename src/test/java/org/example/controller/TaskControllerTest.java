package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.TaskDTO;
import org.example.entity.Task;
import org.example.entity.TaskStatus;
import org.example.entity.Team;
import org.example.exception.InvalidTaskStatusTransitionException;
import org.example.service.TaskService;
import org.example.controller.TaskController;
import org.example.service.TeamService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(TaskController.class)
public class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @MockBean
    private TeamService teamService;  // Добавляем мок для TeamService

    @Autowired
    private ObjectMapper objectMapper;

    private Task task;
    private Team team;

    @BeforeEach
    void setUp() {
        team = new Team(1L, "Backend Team", List.of(TaskStatus.TODO, TaskStatus.IN_PROGRESS, TaskStatus.REVIEW, TaskStatus.TEST, TaskStatus.DONE), List.of());
        task = new Task(1L, "Some Task", TaskStatus.TODO, LocalDateTime.now(), null, team);
    }

    @Test
    void testCreateTask() throws Exception {
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setDescription("Some Task");
        taskDTO.setStatus(String.valueOf(TaskStatus.TODO));
        taskDTO.setTeamId(1L);

        when(taskService.createTask(any(Task.class))).thenReturn(task);
        when(teamService.getTeamByIdWithTasks(1L)).thenReturn(team);  // Мокаем метод для получения команды

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskDTO))) // Передаем TaskDto, а не Task
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Some Task"));
    }

    @Test
    void testUpdateTaskStatus() throws Exception {
        Task updatedTask = new Task(1L, "Some Task", TaskStatus.IN_PROGRESS, LocalDateTime.now(), LocalDateTime.now(), team);
        when(taskService.updateTaskStatus(1L, TaskStatus.IN_PROGRESS)).thenReturn(updatedTask);

        mockMvc.perform(patch("/api/tasks/1/status")
                        .param("status", "IN_PROGRESS"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }

    @Test
    void testUpdateTaskStatus_InvalidTransition() throws Exception {
        doThrow(new InvalidTaskStatusTransitionException("Invalid status transition"))
                .when(taskService).updateTaskStatus(1L, TaskStatus.IN_PROGRESS);

        mockMvc.perform(patch("/api/tasks/1/status")
                        .param("status", "IN_PROGRESS"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value("Invalid status transition"));
    }

    @Test
    void testAssignTeamToTask() throws Exception {
        Team teamWithId = new Team(1L, "Backend Team", List.of(TaskStatus.TODO, TaskStatus.IN_PROGRESS), List.of());
        Task updatedTask = new Task(1L, "Some Task", TaskStatus.TODO, LocalDateTime.now(), LocalDateTime.now(), teamWithId);

        when(taskService.assignTeamToTask(1L, 1L)).thenReturn(updatedTask);

        mockMvc.perform(patch("/api/tasks/1/team")
                        .param("teamId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.team.id").value(1));
    }

    @Test
    void testGetTaskById() throws Exception {
        when(taskService.getTaskById(1L)).thenReturn(task);

        mockMvc.perform(get("/api/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Some Task"));
    }

    @Test
    void testGetAllTasks() throws Exception {
        List<Task> tasks = List.of(task);
        when(taskService.getAllTasks()).thenReturn(tasks);

        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].description").value("Some Task"));
    }
}
