package org.example.repository;

import org.example.entity.TaskStatus;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static org.jooq.generated.Tables.TASK_STATUS;

@Repository
public class TaskStatusRepository {

    private final DSLContext dsl;

    public TaskStatusRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    public List<TaskStatus> findAll() {
        return dsl.selectFrom(TASK_STATUS)
                .fetch(this::mapRecordToTaskStatus);
    }

    public Optional<TaskStatus> findById(Long id) {
        Record record = dsl.selectFrom(TASK_STATUS)
                .where(TASK_STATUS.ID.eq((long) Math.toIntExact(id)))
                .fetchOne();

        return record != null ? Optional.of(mapRecordToTaskStatus(record)) : Optional.empty();
    }

    public Optional<TaskStatus> findByName(String name) {
        Record record = dsl.selectFrom(TASK_STATUS)
                .where(TASK_STATUS.NAME.eq(name))
                .fetchOne();

        return record != null ? Optional.of(mapRecordToTaskStatus(record)) : Optional.empty();
    }
    public List<TaskStatus> findByTeamId(Long teamId) {
        return dsl.selectFrom(TASK_STATUS)
                .where(TASK_STATUS.TEAM_ID.eq((long) Math.toIntExact(teamId)))
                .fetch(this::mapRecordToTaskStatus);
    }


    private TaskStatus mapRecordToTaskStatus(Record record) {
        TaskStatus taskStatus = new TaskStatus();
        taskStatus.setId(Long.valueOf(record.getValue(TASK_STATUS.ID))); // Убедитесь, что есть соответствующие геттеры и сеттеры
        taskStatus.setName(record.getValue(TASK_STATUS.NAME)); // Предполагается, что есть поле NAME

        return taskStatus;
    }
}
