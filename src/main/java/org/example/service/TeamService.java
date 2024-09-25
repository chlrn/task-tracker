package org.example.service;

import org.example.entity.Team;
import org.example.repository.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TeamService {

    @Autowired
    private TeamRepository teamRepository;

    public Team createTeam(Team team) {
        return teamRepository.save(team);
    }

    // Метод для получения всех команд с задачами
    public List<Team> getAllTeamsWithTasks() {
        return teamRepository.findAll();
    }

    // Метод для получения команды по её идентификатору с задачами
    public Team getTeamByIdWithTasks(Long teamId) {
        Optional<Team> teamOpt = teamRepository.findById(teamId);
        return teamOpt.orElseThrow(() -> new RuntimeException("Team not found"));
    }
}
