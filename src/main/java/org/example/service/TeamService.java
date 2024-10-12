package org.example.service;

import jakarta.transaction.Transactional;
import org.example.dto.TeamDtoRequest;
import org.example.dto.TeamDtoResponse;
import org.example.entity.User;
import org.example.exception.ResourceNotFoundException;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.generated.tables.Team;
import org.jooq.generated.tables.TeamManagers; // Добавлено для работы с менеджерами
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.jooq.generated.Tables.TASK_STATUS;
import static org.jooq.generated.Tables.TEAM;

@Service
public class TeamService {

    private final DSLContext dsl;

    public TeamService(DSLContext dsl) {
        this.dsl = dsl;
    }

    public TeamDtoResponse createTeam(TeamDtoRequest teamDtoRequest) {
        // Вставляем команду в таблицу TEAM
        Long teamId = dsl.insertInto(TEAM)
                .set(TEAM.NAME, teamDtoRequest.getName())
                .returning(TEAM.ID)
                .fetchOne()
                .getValue(TEAM.ID);

        // Если указаны рабочие процессы, добавляем их в таблицу task_status
        if (teamDtoRequest.getWorkflows() != null) {
            for (String workflow : teamDtoRequest.getWorkflows()) {
                dsl.insertInto(TASK_STATUS)
                        .set(TASK_STATUS.NAME, workflow) // Сохраняем имя статуса
                        .set(TASK_STATUS.TEAM_ID, teamId) // Устанавливаем связь с командой
                        .execute();
            }
        }

        // Возвращаем данные созданной команды
        return getTeamById(teamId);
    }

    public List<TeamDtoResponse> getAllTeamsWithTasks() {
        Result<Record> result = dsl.select()
                .from(TEAM)
                .leftJoin(TASK_STATUS).on(TEAM.ID.eq(TASK_STATUS.TEAM_ID))
                .fetch();

        Map<Long, TeamDtoResponse> teamsMap = new HashMap<>();

        result.forEach(record -> {
            Long teamId = record.get(TEAM.ID);
            TeamDtoResponse teamDtoResponse = teamsMap.computeIfAbsent(teamId, id -> {
                TeamDtoResponse dto = new TeamDtoResponse();
                dto.setId(id);
                dto.setName(record.get(TEAM.NAME));
                dto.setWorkflows(new ArrayList<>()); // Инициализируем список для воркфлоу
                return dto;
            });

            String workflow = record.get(TASK_STATUS.NAME);
            if (workflow != null) {
                teamDtoResponse.getWorkflows().add(workflow);
            }
        });

        return new ArrayList<>(teamsMap.values());
    }

    public TeamDtoResponse getTeamById(Long id) {
        Record record = dsl.select().from(TEAM).where(TEAM.ID.eq(id)).fetchOne();
        if (record == null) {
            throw new ResourceNotFoundException("Команда не найдена");
        }

        TeamDtoResponse teamDtoResponse = convertToResponseDTO(record);

        List<String> workflows = dsl.select(TASK_STATUS.NAME)
                .from(TASK_STATUS)
                .where(TASK_STATUS.TEAM_ID.eq(id))
                .fetch(TASK_STATUS.NAME);

        teamDtoResponse.setWorkflows(workflows);

        return teamDtoResponse;
    }

    public void addManager(Long teamId, Long userId) {
        // Проверяем существование команды и пользователя
        if (dsl.fetchCount(dsl.selectFrom(TEAM).where(TEAM.ID.eq(teamId))) == 0) {
            throw new ResourceNotFoundException("Команда не найдена");
        }

        if (dsl.fetchCount(dsl.selectFrom("users").where("id = ?", userId)) == 0) {
            throw new ResourceNotFoundException("Пользователь не найден");
        }

        // Добавляем менеджера в команду
        dsl.insertInto(TeamManagers.TEAM_MANAGERS) // Таблица для связи менеджеров с командами
                .set(TeamManagers.TEAM_MANAGERS.TEAM_ID, teamId)
                .set(TeamManagers.TEAM_MANAGERS.MANAGER_ID, userId)
                .execute();
    }

    private TeamDtoResponse convertToResponseDTO(Record teamRecord) {
        TeamDtoResponse dto = new TeamDtoResponse();
        dto.setId(teamRecord.getValue(TEAM.ID));
        dto.setName(teamRecord.getValue(TEAM.NAME));
        return dto;
    }
}
