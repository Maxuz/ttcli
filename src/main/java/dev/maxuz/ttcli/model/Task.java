package dev.maxuz.ttcli.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Task {
    /**
     * Unique identifier of this task
     */
    private String code;

    /**
     * Task state
     */
    private TaskState state;

}
