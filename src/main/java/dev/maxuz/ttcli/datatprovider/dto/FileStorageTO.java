package dev.maxuz.ttcli.datatprovider.dto;

import lombok.Data;

import java.util.List;

@Data
public class FileStorageTO {
    private List<TaskTO> tasks;
}
