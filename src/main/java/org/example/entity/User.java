package org.example.entity;



import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Table(name = "users")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email; // Яндекс почта

    @Column(nullable = false)
    private String name;

    // Связь с ролями
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles;

    // Команды, которые создал пользователь
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Team> ownedTeams;

    // Команды, в которых пользователь является менеджером
    @ManyToMany(mappedBy = "managers")
    private Set<Team> managedTeams;

    // Задачи, которые назначены пользователю (опционально)
    @OneToMany(mappedBy = "assignedTo")
    private Set<Task> assignedTasks;
}

