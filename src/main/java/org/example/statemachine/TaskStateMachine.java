package org.example.statemachine;

import org.example.entity.TaskStatus;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class TaskStateMachine {

    private final List<TaskStatus> workflow = Arrays.asList(
            TaskStatus.TODO,
            TaskStatus.IN_PROGRESS,
            TaskStatus.REVIEW,
            TaskStatus.TEST,
            TaskStatus.DONE
    );

    public boolean canTransition(TaskStatus currentStatus, TaskStatus newStatus) {
        int currentIndex = workflow.indexOf(currentStatus);
        int newIndex = workflow.indexOf(newStatus);

        // Разрешаем переход назад или на следующий статус, но запрещаем перепрыгивать
        return currentIndex != -1 && newIndex != -1 &&
                (newIndex == currentIndex + 1 || newIndex == currentIndex - 1);
    }
}
