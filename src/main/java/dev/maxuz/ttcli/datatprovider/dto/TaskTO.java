package dev.maxuz.ttcli.datatprovider.dto;

import lombok.Data;

@Data
public class TaskTO {
    private String code;

    private TaskStateTO state;
}
