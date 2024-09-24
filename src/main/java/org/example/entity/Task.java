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
//@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@JsonInclude(JsonInclude.Include.NON_NULL) // Только ненулевые значения

public class Task {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String description;


        @Enumerated(EnumType.STRING)
        private TaskStatus status;

        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        @ManyToOne
        @JsonIgnoreProperties({"tasks"}) // Игнорируем поле задач в JSON представлении команды
        @JoinColumn(name = "team_id")
        private Team team;


}

