package org.example.dto;


import lombok.Data;
import java.util.List;

@Data
public class TeamDtoRequest {

    private String name;
    private List<String> workflows;

}
