package dev.maxuz.ttcli.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Task {
    /**
     * Unique name of this task
     */
    private String name;

    /**
     * Task state
     */
    private TaskState state;

    /**
     * The amount of time (in milliseconds) you have already spent on this task
     */
    private long timeSpent;

    /**
     * This time must be not null only if the {@name state} = {@name TaskState.IN_PROGRESS}.
     * This time is necessary for calculating total time and {@name timeSpent} when you stop this task.
     * The total time spent on this task = (now - {@name startTime} + {@name timeSpent}.
     */
    private Long startTime;

}
