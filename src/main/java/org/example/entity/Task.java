package org.example.entity;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "task")
public class Task {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(name = "description")
        private String description;

        @ManyToOne
        @JoinColumn(name = "status_id", referencedColumnName = "id", nullable = false)
        private TaskStatus status;  // Связь с таблицей task_status

        @ManyToOne
        @JoinColumn(name = "team_id", referencedColumnName = "id")
        @JsonIgnoreProperties("tasks")  // Игнорируем обратную ссылку на задачи
        private Team team;  // Связь ManyToOne с Team

        @Column(name = "created_at", nullable = false, updatable = false)
        private LocalDateTime createdAt;

        @Column(name = "updated_at")
        private LocalDateTime updatedAt;

        @ManyToOne
        @JoinColumn(name = "assigned_to_id", referencedColumnName = "id")
        @JsonIgnoreProperties({"ownedTeams", "managedTeams", "roles"})
        private User assignedTo; // Пользователь, которому назначена задача

        // Обработчики для автоматического обновления дат
        @PrePersist
        protected void onCreate() {
                this.createdAt = LocalDateTime.now();
                this.updatedAt = LocalDateTime.now();
        }

        @PreUpdate
        protected void onUpdate() {
                this.updatedAt = LocalDateTime.now();
        }
}
