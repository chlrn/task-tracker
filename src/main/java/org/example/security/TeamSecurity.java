package org.example.security;

import org.example.entity.Team;
import org.example.entity.User;
import org.example.repository.TeamRepository;
import org.example.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component("teamSecurity")
@RequiredArgsConstructor
public class TeamSecurity {

    private final TeamRepository teamRepository;
    private final UserRepository userRepository;

    public boolean isTeamOwner(Authentication authentication, Long teamId) {
        String userEmail = authentication.getName();
        User user = userRepository.findByEmail(userEmail).orElse(null);
        if (user == null) return false;

        Team team = teamRepository.findById(teamId).orElse(null);
        if (team == null) return false;

        return team.getOwner().getId().equals(user.getId());
    }
}
