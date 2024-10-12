package org.example.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.example.entity.Task;
import org.example.entity.TaskStatus;
import org.jooq.DSLContext;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class TaskRepository {

    private final DSLContext dsl;

    @PersistenceContext
    private EntityManager entityManager;

    public TaskRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    @Transactional
    public Task save(Task task) {
        // Сохраняем статус задачи, если он новый
        if (task.getStatus() != null) {
            TaskStatus status = entityManager.merge(task.getStatus());
            task.setStatus(status);
        }

        entityManager.persist(task); // Сохраняем задачу
        return task; // Возвращаем сохранённую задачу
    }

    public Optional<Task> findById(Long taskId) {
        Task task = entityManager.find(Task.class, taskId);
        return Optional.ofNullable(task);
    }

    public List<Task> findAll() {
        return entityManager.createQuery("SELECT t FROM Task t", Task.class).getResultList();
    }

    public List<Task> findByTeamId(Long teamId) {
        return entityManager.createQuery("SELECT t FROM Task t WHERE t.team.id = :teamId", Task.class)
                .setParameter("teamId", teamId)
                .getResultList();
    }
}
