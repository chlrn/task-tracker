package org.example.service;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.TaskDTO;
import org.example.entity.TaskStatus;
import org.example.exception.InvalidTaskStatusTransitionException;
import org.example.exception.ResourceNotFoundException;
import org.example.repository.TaskStatusRepository; // Импортируйте репозиторий
import org.example.statemachine.TaskStateMachine;
import org.hibernate.service.spi.ServiceException;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import static org.jooq.generated.Tables.TASK;
import static org.jooq.generated.Tables.TEAM;

@Slf4j
@Data
@Service
public class TaskService {

    private final DSLContext dsl;
    private final TaskStateMachine taskStateMachine;
    private final TaskStatusRepository taskStatusRepository; // Добавление репозитория

    @Autowired
    public TaskService(DSLContext dsl, TaskStateMachine taskStateMachine, TaskStatusRepository taskStatusRepository) {
        this.dsl = dsl;
        this.taskStateMachine = taskStateMachine;
        this.taskStatusRepository = taskStatusRepository; // Инициализация репозитория
    }

    public TaskDTO createTaskFromDTO(TaskDTO taskDTO) {
        try {
            TaskStatus status = validateTaskStatus(taskDTO.getStatus()); // Получаем статус из БД
            Long teamId = taskDTO.getTeamId();
            Long generatedTaskId = dsl
                    .insertInto(TASK)
                    .set(TASK.DESCRIPTION, taskDTO.getDescription())
                    .set(TASK.STATUS, status.getName()) // Используем имя статуса
                    .set(TASK.TEAM_ID, teamId)
                    .set(TASK.CREATED_AT, LocalDateTime.now())
                    .returning(TASK.ID)
                    .fetchOne()
                    .getValue(TASK.ID);

            log.info("Task created with ID: {}", generatedTaskId);
            return getTaskById(generatedTaskId);
        } catch (IllegalArgumentException e) {
            log.error("Invalid input: {}", e.getMessage());
            throw new ServiceException("Invalid input", e);
        } catch (NoSuchElementException e) {
            log.error("Team not found: {}", e.getMessage());
            throw new ServiceException("Team not found", e);
        } catch (Exception e) {
            log.error("Unexpected error: ", e);
            throw new ServiceException("Unexpected error", e);
        }
    }

    private TaskStatus validateTaskStatus(String statusStr) {
        return taskStatusRepository.findByName(statusStr)
                .orElseThrow(() -> {
                    log.error("Invalid task status: {}", statusStr);
                    return new IllegalArgumentException("Invalid task status: " + statusStr);
                });
    }


    public ResponseEntity<TaskDTO> updateTaskStatus(Long taskId, String statusStr) {
        try {
            TaskStatus newStatus = validateTaskStatus(statusStr); // Получаем новый статус из БД
            TaskDTO updatedTask = updateTaskStatusLogic(taskId, newStatus);
            return ResponseEntity.ok(updatedTask);
        } catch (InvalidTaskStatusTransitionException e) {
            log.error("Invalid status transition for task {}: {}", taskId, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (IllegalArgumentException e) {
            log.error("Invalid task status provided: {}", statusStr);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (ResourceNotFoundException e) {
            log.error("Task not found for ID {}: {}", taskId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            log.error("Unexpected error occurred: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    private TaskDTO updateTaskStatusLogic(Long taskId, TaskStatus newStatus) {
        TaskDTO taskDTO = getTaskById(taskId); // Проверяем, существует ли задача

        // Извлекаем текущий статус из БД
        TaskStatus currentStatus = taskStatusRepository.findByName(taskDTO.getStatus())
                .orElseThrow(() -> new ResourceNotFoundException("Current status not found"));

        // Извлекаем teamId из задачи
        Long teamId = taskDTO.getTeamId();

        // Передаем teamId в метод canTransition
        if (taskStateMachine.canTransition(currentStatus, newStatus, teamId)) {
            // Если возможен переход статуса, обновляем его в базе данных
            dsl.update(TASK)
                    .set(TASK.STATUS, newStatus.getName())
                    .set(TASK.UPDATED_AT, LocalDateTime.now())
                    .where(TASK.ID.eq(taskId))
                    .execute();
            return getTaskById(taskId); // Возвращаем обновленную задачу
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
        dto.setStatus(taskRecord.getValue(TASK.STATUS)); // Статус теперь строка, которую мы получаем из БД
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
