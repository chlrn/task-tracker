package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.dto.TeamDTO;
import org.example.service.TeamService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Team Controller", description = "API для управления командами")
@RestController
@RequestMapping("/api/teams")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;

    @Operation(summary = "Создать команду")
    @PostMapping
    public ResponseEntity<TeamDTO> createTeam(@RequestBody TeamDTO teamDTO) {
        TeamDTO createdTeamDTO = teamService.createTeam(teamDTO);
        return ResponseEntity.ok(createdTeamDTO);
    }

    @Operation(summary = "Получить все команды")
    @GetMapping
    public ResponseEntity<List<TeamDTO>> getAllTeams() {
        List<TeamDTO> teamDTOs = teamService.getAllTeamsWithTasks();
        return ResponseEntity.ok(teamDTOs);
    }

    @Operation(summary = "Получить команду по ее ID")
    @GetMapping("/{id}")
    public ResponseEntity<TeamDTO> getTeamById(@PathVariable Long id) {
        TeamDTO teamDTO = teamService.getTeamById(id); // Измените здесь
        return ResponseEntity.ok(teamDTO);
    }
}
