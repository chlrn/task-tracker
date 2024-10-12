package org.example.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.jooq.generated.tables.TeamWorkflow;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "team")
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = false)
    @JoinColumn(name = "team_id") // Связываем с таблицей team_workflow по team_id
    @JsonIgnoreProperties("team")
    private List<TaskStatus> workflows;

    @OneToMany(mappedBy = "team", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnoreProperties("team")
    private List<Task> tasks;

    // Владелец команды
    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    @JsonIgnoreProperties({"ownedTeams", "managedTeams", "roles"})
    private User owner;

    // Менеджеры команды
    @ManyToMany
    @JoinTable(
            name = "teams_managers",
            joinColumns = @JoinColumn(name = "team_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @JsonIgnoreProperties({"ownedTeams", "managedTeams", "roles"})
    private Set<User> managers;
}
