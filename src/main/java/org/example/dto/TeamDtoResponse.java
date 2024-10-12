package org.example.dto;

import lombok.Data;
import java.util.List;

@Data
public class TeamDtoResponse {

    private Long id;
    private String name;
    private List<String> workflows;
    private List<String> managersEmails; // Список email менеджеров команды


}
