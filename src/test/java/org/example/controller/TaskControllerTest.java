package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.TaskDTO;
import org.example.entity.Task;
import org.example.entity.TaskStatus;
import org.example.entity.Team;
import org.example.exception.InvalidTaskStatusTransitionException;
import org.example.service.TaskService;
import org.example.service.TeamService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

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

    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @MockBean
    private TeamService teamService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private Task task;
    private TaskDTO taskDTO;
    private Team team;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);  // Инициализируем моки

        // Создаем экземпляры Task и TaskDTO
        team = new Team(1L, "Backend Team", List.of(TaskStatus.TODO, TaskStatus.IN_PROGRESS, TaskStatus.REVIEW, TaskStatus.TEST, TaskStatus.DONE), List.of());

        task = new Task(1L, "Some Task", TaskStatus.TODO, LocalDateTime.now(), null, team);

        taskDTO = new TaskDTO();
        taskDTO.setId(task.getId());
        taskDTO.setDescription(task.getDescription());
        taskDTO.setStatus(task.getStatus().name());
        taskDTO.setCreatedAt(task.getCreatedAt());
        taskDTO.setUpdatedAt(task.getUpdatedAt());
        taskDTO.setTeamId(task.getTeam() != null ? task.getTeam().getId() : null);

        mockMvc = MockMvcBuilders.standaloneSetup(new TaskController(taskService, teamService)).build();  // Инициализируем MockMvc вручную
    }

    @Test
    void testCreateTask() throws Exception {
        when(taskService.createTaskFromDTO(any(TaskDTO.class))).thenReturn(task);
        when(teamService.getTeamByIdWithTasks(1L)).thenReturn(team);

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(taskDTO.getId()))
                .andExpect(jsonPath("$.description").value(taskDTO.getDescription()))
                .andExpect(jsonPath("$.status").value(taskDTO.getStatus()))
                .andExpect(jsonPath("$.teamId").value(taskDTO.getTeamId()));
    }

    @Test
    void testUpdateTaskStatus() throws Exception {
        Task updatedTask = new Task(1L, "Some Task", TaskStatus.IN_PROGRESS, LocalDateTime.now(), LocalDateTime.now(), team);
        TaskDTO updatedTaskDTO = new TaskDTO();
        updatedTaskDTO.setId(updatedTask.getId());
        updatedTaskDTO.setDescription(updatedTask.getDescription());
        updatedTaskDTO.setStatus(updatedTask.getStatus().name());
        updatedTaskDTO.setCreatedAt(updatedTask.getCreatedAt());
        updatedTaskDTO.setUpdatedAt(updatedTask.getUpdatedAt());
        updatedTaskDTO.setTeamId(updatedTask.getTeam() != null ? updatedTask.getTeam().getId() : null);

        when(taskService.updateTaskStatus(1L, TaskStatus.IN_PROGRESS)).thenReturn(updatedTask);

        mockMvc.perform(patch("/api/tasks/1/status")
                        .param("status", "IN_PROGRESS"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(updatedTaskDTO.getId()))
                .andExpect(jsonPath("$.description").value(updatedTaskDTO.getDescription()))
                .andExpect(jsonPath("$.status").value(updatedTaskDTO.getStatus()))
                .andExpect(jsonPath("$.teamId").value(updatedTaskDTO.getTeamId()));
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
        Task updatedTask = new Task(1L, "Some Task", TaskStatus.TODO, LocalDateTime.now(), LocalDateTime.now(), team);
        TaskDTO updatedTaskDTO = new TaskDTO();
        updatedTaskDTO.setId(updatedTask.getId());
        updatedTaskDTO.setDescription(updatedTask.getDescription());
        updatedTaskDTO.setStatus(updatedTask.getStatus().name());
        updatedTaskDTO.setCreatedAt(updatedTask.getCreatedAt());
        updatedTaskDTO.setUpdatedAt(updatedTask.getUpdatedAt());
        updatedTaskDTO.setTeamId(updatedTask.getTeam() != null ? updatedTask.getTeam().getId() : null);

        when(taskService.assignTeamToTask(1L, 1L)).thenReturn(updatedTask);

        mockMvc.perform(patch("/api/tasks/1/team")
                        .param("teamId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(updatedTaskDTO.getId()))
                .andExpect(jsonPath("$.description").value(updatedTaskDTO.getDescription()))
                .andExpect(jsonPath("$.status").value(updatedTaskDTO.getStatus()))
                .andExpect(jsonPath("$.teamId").value(updatedTaskDTO.getTeamId()));
    }

    @Test
    void testGetTaskById() throws Exception {
        when(taskService.getTaskById(1L)).thenReturn(task);

        mockMvc.perform(get("/api/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(taskDTO.getId()))
                .andExpect(jsonPath("$.description").value(taskDTO.getDescription()))
                .andExpect(jsonPath("$.status").value(taskDTO.getStatus()))
                .andExpect(jsonPath("$.teamId").value(taskDTO.getTeamId()));
    }

    @Test
    void testGetAllTasks() throws Exception {
        List<Task> tasks = List.of(task);
        when(taskService.getAllTasks()).thenReturn(tasks);

        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(taskDTO.getId()))
                .andExpect(jsonPath("$[0].description").value(taskDTO.getDescription()))
                .andExpect(jsonPath("$[0].status").value(taskDTO.getStatus()))
                .andExpect(jsonPath("$[0].teamId").value(taskDTO.getTeamId()));
    }
}
