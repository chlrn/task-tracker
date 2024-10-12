package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.dto.TeamDtoRequest;
import org.example.dto.TeamDtoResponse;
import org.example.service.TeamService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Team Controller", description = "API для управления командами")
@RestController
@RequestMapping("/api/teams")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;

    @Operation(summary = "Создать команду")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<TeamDtoResponse> createTeam(@RequestBody TeamDtoRequest teamDtoRequest) {
        TeamDtoResponse createdTeamDTO = teamService.createTeam(teamDtoRequest);
        return ResponseEntity.ok(createdTeamDTO);
    }

    @Operation(summary = "Получить все команды")
    @GetMapping
    public ResponseEntity<List<TeamDtoResponse>> getAllTeams() {
        List<TeamDtoResponse> teamDTOs = teamService.getAllTeamsWithTasks();
        return ResponseEntity.ok(teamDTOs);
    }

    @Operation(summary = "Получить команду по ее ID")
    @GetMapping("/{id}")
    public ResponseEntity<TeamDtoResponse> getTeamById(@PathVariable Long id) {
        TeamDtoResponse teamDTO = teamService.getTeamById(id);
        return ResponseEntity.ok(teamDTO);
    }

    @Operation(summary = "Добавить менеджера в команду")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{teamId}/managers")
    public ResponseEntity<?> addManagerToTeam(@PathVariable Long teamId, @RequestParam Long userId) {
        teamService.addManager(teamId, userId);
        return ResponseEntity.ok("Manager added");
    }
}
