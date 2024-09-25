package org.example.service;

import lombok.Data;
import org.example.dto.TaskDTO;
import org.example.entity.TaskStatus;
import org.example.exception.InvalidTaskStatusTransitionException;
import org.example.exception.ResourceNotFoundException;
import org.example.statemachine.TaskStateMachine;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jooq.Record;
import org.jooq.Result;

import java.time.LocalDateTime;
import java.util.List;

import static org.jooq.generated.Tables.TASK;
import static org.jooq.generated.Tables.TEAM;

@Data
@Service
public class TaskService {

    private static final Logger logger = LoggerFactory.getLogger(TaskService.class);

    private final DSLContext dsl;
    private final TaskStateMachine taskStateMachine;

    @Autowired
    public TaskService(DSLContext dsl, TaskStateMachine taskStateMachine) {
        this.dsl = dsl;
        this.taskStateMachine = taskStateMachine;
    }

    public TaskDTO createTaskFromDTO(TaskDTO taskDTO) {
        String status = validateTaskStatus(taskDTO.getStatus());
        Long teamId = taskDTO.getTeamId();
        Long generatedTaskId = dsl
                .insertInto(TASK)
                .set(TASK.DESCRIPTION, taskDTO.getDescription())
                .set(TASK.STATUS, status)
                .set(TASK.TEAM_ID, teamId)
                .set(TASK.CREATED_AT, LocalDateTime.now())
                .returning(TASK.ID)
                .fetchOne()
                .getValue(TASK.ID);

        logger.info("Task created with ID: {}", generatedTaskId);
        return getTaskById(generatedTaskId);
    }

    private String validateTaskStatus(String statusStr) {
        try {
            TaskStatus.valueOf(statusStr);
            return statusStr;
        } catch (IllegalArgumentException e) {
            logger.error("Invalid task status: {}", statusStr);
            throw new IllegalArgumentException("Invalid task status: " + statusStr);
        }
    }

    public TaskDTO updateTaskStatus(Long taskId, TaskStatus newStatus) {
        TaskDTO taskDTO = getTaskById(taskId);
        TaskStatus currentStatus = TaskStatus.valueOf(taskDTO.getStatus());

        if (taskStateMachine.canTransition(currentStatus, newStatus)) {
            dsl.update(TASK)
                    .set(TASK.STATUS, newStatus.name())
                    .set(TASK.UPDATED_AT, LocalDateTime.now())
                    .where(TASK.ID.eq(taskId))
                    .execute();
            return getTaskById(taskId);
        } else {
            throw new InvalidTaskStatusTransitionException("Invalid status transition");
        }
    }

    public TaskDTO assignTeamToTask(Long taskId, Long teamId) {
        getTeamById(teamId);
        dsl.update(TASK)
                .set(TASK.TEAM_ID, teamId)
                .set(TASK.UPDATED_AT, LocalDateTime.now())
                .where(TASK.ID.eq(taskId))
                .execute();
        return getTaskById(taskId);
    }

    public List<TaskDTO> getAllTasks() {
        Result<Record> result = dsl.select().from(TASK).fetch();
        return result.map(record -> convertToDTO(record.into(TASK)));
    }

    public TaskDTO getTaskById(Long taskId) {
        Record record = dsl.select().from(TASK).where(TASK.ID.eq(taskId)).fetchOne();
        if (record == null) {
            throw new ResourceNotFoundException("Task not found");
        }
        return convertToDTO(record.into(TASK));
    }

    public List<TaskDTO> getTasksByTeamId(Long teamId) {
        getTeamById(teamId);
        Result<Record> result = dsl.select().from(TASK).where(TASK.TEAM_ID.eq(teamId)).fetch();
        return result.map(record -> convertToDTO(record.into(TASK)));
    }

    public TaskDTO convertToDTO(Record taskRecord) {
        TaskDTO dto = new TaskDTO();
        dto.setId(taskRecord.getValue(TASK.ID));
        dto.setDescription(taskRecord.getValue(TASK.DESCRIPTION));
        dto.setStatus(taskRecord.getValue(TASK.STATUS));
        dto.setCreatedAt(taskRecord.getValue(TASK.CREATED_AT));
        dto.setUpdatedAt(taskRecord.getValue(TASK.UPDATED_AT));
        dto.setTeamId(taskRecord.getValue(TASK.TEAM_ID));
        return dto;
    }

    private void getTeamById(Long teamId) {
        if (dsl.selectCount().from(TEAM).where(TEAM.ID.eq(teamId)).fetchOne(0, Integer.class) == 0) {
            throw new ResourceNotFoundException("Team not found");
        }
    }
}
