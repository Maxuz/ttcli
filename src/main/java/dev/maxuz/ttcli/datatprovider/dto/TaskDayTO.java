package dev.maxuz.ttcli.datatprovider.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TaskDayTO {
    private String date;

    private List<TaskTO> tasks;
}
