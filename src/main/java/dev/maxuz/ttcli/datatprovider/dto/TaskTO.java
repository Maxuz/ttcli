package dev.maxuz.ttcli.datatprovider.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TaskTO {
    private String code;

    private TaskStateTO state;

    private long timeSpent;

    private Long startTime;
}
