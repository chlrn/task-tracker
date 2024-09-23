package org.example.repository;

import org.example.entity.Task;
import org.example.entity.TaskStatus;
import org.jooq.DSLContext;
import org.jooq.generated.tables.records.TaskRecord;
import org.springframework.stereotype.Repository;
import static org.jooq.generated.Tables.TASK;
import java.util.List;
import java.util.Optional;

@Repository
public class TaskRepository {

    private final DSLContext dsl;

    public TaskRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    public Task save(Task task) {
        TaskRecord taskRecord = dsl.insertInto(TASK.TASK)
                .set(TASK.TASK.DESCRIPTION, task.getDescription())
                .set(TASK.TASK.STATUS, task.getStatus().name())
                .set(TASK.TASK.TEAM_ID, task.getTeam() != null ? task.getTeam().getId() : null)
                .set(TASK.TASK.CREATED_AT, task.getCreatedAt())
                .set(TASK.TASK.UPDATED_AT, task.getUpdatedAt())
                .returning()
                .fetchOne();

        return mapRecordToTask(taskRecord);
    }

    public Optional<Task> findById(Long taskId) {
        TaskRecord taskRecord = dsl.selectFrom(TASK.TASK)
                .where(TASK.TASK.ID.eq(taskId))
                .fetchOne();

        return taskRecord != null ? Optional.of(mapRecordToTask(taskRecord)) : Optional.empty();
    }

    public List<Task> findAll() {
        return dsl.selectFrom(TASK.TASK)
                .fetch()
                .map(this::mapRecordToTask);
    }

    public List<Task> findByTeamId(Long teamId) {
        return dsl.selectFrom(TASK.TASK)
                .where(TASK.TASK.TEAM_ID.eq(teamId))
                .fetch()
                .map(this::mapRecordToTask);
    }

    private Task mapRecordToTask(TaskRecord record) {
        Task task = new Task();
        task.setId(record.getId());
        task.setDescription(record.getDescription());
        task.setStatus(TaskStatus.valueOf(record.getStatus()));
        task.setCreatedAt(record.getCreatedAt());
        task.setUpdatedAt(record.getUpdatedAt());
        return task;
    }
}
