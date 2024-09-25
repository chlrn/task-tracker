package org.example.statemachine;

import org.example.entity.TaskStatus;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TaskStateMachine {

    public boolean canTransition(TaskStatus currentStatus, TaskStatus newStatus, List<TaskStatus> workflow) {
        int currentIndex = workflow.indexOf(currentStatus);
        int newIndex = workflow.indexOf(newStatus);

        // Разрешаем переход назад или на следующий статус, но запрещаем перепрыгивать
        return currentIndex != -1 && newIndex != -1 &&
                (newIndex == currentIndex + 1 || newIndex == currentIndex - 1);
    }
}
