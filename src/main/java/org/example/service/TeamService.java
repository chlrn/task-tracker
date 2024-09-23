package org.example.service;

import org.example.dto.TeamDTO;
import org.example.exception.ResourceNotFoundException;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.jooq.generated.Tables.TEAM;
import static org.jooq.generated.tables.TeamWorkflow.TEAM_WORKFLOW;

@Service
public class TeamService {

    private final DSLContext dsl;

    public TeamService(DSLContext dsl) {
        this.dsl = dsl;
    }

    public TeamDTO createTeam(TeamDTO teamDTO) {
        Long teamId = dsl.insertInto(TEAM)
                .set(TEAM.NAME, teamDTO.getName())
                .returning(TEAM.ID)
                .fetchOne()
                .getValue(TEAM.ID);

        // Если указаны рабочие процессы, добавляем их в таблицу team_workflow
        if (teamDTO.getWorkflows() != null) {
            for (String workflow : teamDTO.getWorkflows()) {
                dsl.insertInto(TEAM_WORKFLOW)
                        .set(TEAM_WORKFLOW.TEAM_ID, teamId)
                        .set(TEAM_WORKFLOW.WORKFLOW, workflow)
                        .execute();
            }
        }

        return getTeamById(teamId);
    }

    public List<TeamDTO> getAllTeamsWithTasks() {
        Result<Record> result = dsl.select().from(TEAM).fetch();
        return result.map(record -> convertToDTO(record.into(TEAM)));
    }

    public TeamDTO getTeamById(Long id) {
        Record record = dsl.select().from(TEAM).where(TEAM.ID.eq(id)).fetchOne();
        if (record == null) {
            throw new ResourceNotFoundException("Team not found");
        }

        TeamDTO teamDTO = convertToDTO(record.into(TEAM));

        // Получаем связанные рабочие процессы
        List<String> workflows = dsl.select(TEAM_WORKFLOW.WORKFLOW)
                .from(TEAM_WORKFLOW)
                .where(TEAM_WORKFLOW.TEAM_ID.eq(id))
                .fetch(TEAM_WORKFLOW.WORKFLOW);

        teamDTO.setWorkflows(workflows);

        return teamDTO;
    }

    private TeamDTO convertToDTO(Record teamRecord) {
        TeamDTO dto = new TeamDTO();
        dto.setId(teamRecord.getValue(TEAM.ID));
        dto.setName(teamRecord.getValue(TEAM.NAME));
        return dto;
    }
}
