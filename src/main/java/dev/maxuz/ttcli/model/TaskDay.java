package dev.maxuz.ttcli.model;

import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
public class TaskDay {
    /**
     * Actual date
     */
    private final LocalDate date;

    /**
     * The list of tasks that we work on for this day
     */
    private List<Task> tasks = new ArrayList<>();

    public TaskDay(LocalDate date) {
        this.date = date;
    }

    public void addTask(Task task) {
        this.tasks.add(task);
    }
}
