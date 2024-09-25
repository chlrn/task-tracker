package org.example.repository;

import org.example.entity.TaskStatus;
import org.example.entity.Team;
import org.jooq.DSLContext;
import org.jooq.generated.tables.records.TeamRecord;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static org.jooq.generated.Tables.TEAM;

@Repository
public class TeamRepository {

    private final DSLContext dsl;

    public TeamRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    public Team save(Team team) {
        TeamRecord teamRecord = dsl.insertInto(TEAM.TEAM)
                .set(TEAM.TEAM.NAME, team.getName())
                .returning()
                .fetchOne();

        return mapRecordToTeam(teamRecord);
    }

    public Optional<Team> findById(Long teamId) {
        TeamRecord teamRecord = dsl.selectFrom(TEAM.TEAM)
                .where(TEAM.TEAM.ID.eq(teamId))
                .fetchOne();

        return teamRecord != null ? Optional.of(mapRecordToTeam(teamRecord)) : Optional.empty();
    }

    public List<Team> findAll() {
        return dsl.selectFrom(TEAM.TEAM)
                .fetch()
                .map(this::mapRecordToTeam);
    }

    private Team mapRecordToTeam(TeamRecord record) {
        Team team = new Team();
        team.setId(record.getId());
        team.setName(record.getName());
        return team;
    }
}
