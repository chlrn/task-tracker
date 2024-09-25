package org.example.dto;

import lombok.Data;
import org.example.entity.TaskStatus;

import java.util.List;

@Data
public class TeamDTO {

    private Long id;
    private String name;
    private List<String> workflows; // Список рабочих процессов


}