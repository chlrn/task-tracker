package org.example.controller;

import org.example.entity.TaskStatus;
import org.example.entity.Team;
import org.example.service.TeamService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@WebMvcTest(TeamController.class)
public class TeamControllerTest {

    @Autowired
    private MockMvc mockMvc;  // Автоматически внедряем MockMvc

    @MockBean
    private TeamService teamService;  // Замена сервиса на мок

    private Team team;

    @BeforeEach
    void setUp() {
        // Инициализация тестовых данных
        team = new Team(1L, "Backend Team", List.of(TaskStatus.TODO, TaskStatus.IN_PROGRESS, TaskStatus.REVIEW, TaskStatus.TEST, TaskStatus.DONE), new ArrayList<>());
    }

    @Test
    void testGetAllTeams() throws Exception {
        List<Team> teams = List.of(team);
        when(teamService.getAllTeamsWithTasks()).thenReturn(teams);  // Мокируем вызов сервиса

        mockMvc.perform(get("/api/teams"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Backend Team"));
    }

    @Test
    void testGetTeamById() throws Exception {
        when(teamService.getTeamByIdWithTasks(1L)).thenReturn(team);  // Мокируем вызов сервиса

        mockMvc.perform(get("/api/teams/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Backend Team"));
    }
}
