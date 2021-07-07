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

    /**
     * The amount of time (in milliseconds) you have already spent on this task
     */
    private long timeSpent;

    /**
     * This time must be not null only if the {@code state} = {@code TaskState.IN_PROGRESS}.
     * This time is necessary for calculating total time and {@code timeSpent} when you stop this task.
     * The total time spent on this task = (now - {@code startTime} + {@code timeSpent}.
     */
    private Long startTime;

}
