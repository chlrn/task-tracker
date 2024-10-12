package org.example.statemachine;

import org.example.entity.TaskStatus;
import org.example.repository.TaskStatusRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TaskStateMachine {

    private final TaskStatusRepository taskStatusRepository;

    public TaskStateMachine(TaskStatusRepository taskStatusRepository) {
        this.taskStatusRepository = taskStatusRepository;
    }

    // Получаем рабочие процессы (workflow) для команды
    public List<TaskStatus> getWorkflowForTeam(Long teamId) {
        return taskStatusRepository.findByTeamId(teamId);
    }

    public boolean canTransition(TaskStatus currentStatus, TaskStatus newStatus, Long teamId) {
        // Загружаем список статусов для команды
        List<TaskStatus> workflow = getWorkflowForTeam(teamId);

        // Проверяем индексы текущего и нового статусов в списке воркфлоу команды
        int currentIndex = workflow.indexOf(currentStatus);
        int newIndex = workflow.indexOf(newStatus);

        // Разрешаем переход только между соседними статусами
        return currentIndex != -1 && newIndex != -1 &&
                (newIndex == currentIndex + 1 || newIndex == currentIndex - 1);
    }

}
