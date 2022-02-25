package dev.maxuz.ttcli.printer;

import dev.maxuz.ttcli.model.Task;
import dev.maxuz.ttcli.model.TaskDay;
import dev.maxuz.ttcli.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.format.DateTimeFormatter;

/**
 * Class for printing messages to a user
 */
@Slf4j
@Service
public class Printer {
    private final TaskService taskService;
    private final TimeConverter timeConverter;

    public Printer(TaskService taskService, TimeConverter timeConverter) {
        this.taskService = taskService;
        this.timeConverter = timeConverter;
    }

    public void info(String message, Object... arguments) {
        log.info(message, arguments);
    }

    public void info(TaskDay taskDay) {
        log.info("Date: {}", taskDay.getDate().format(DateTimeFormatter.ISO_DATE));

        String leftAlignFormat = "| %-15s | %-11s | %-10s |";

        log.info("+-----------------+-------------+------------+");
        log.info("| Name            | State       | Time       |");
        log.info("+-----------------+-------------+------------+");

        long totalTime = 0;
        for (Task task : taskDay.getTasks()) {
            long taskTime = taskService.countTaskTime(task);
            log.info(String.format(leftAlignFormat, task.getName(), task.getState(), timeConverter.convert(taskTime)));
            totalTime += taskTime;
        }
        log.info("+-----------------+-------------+------------+");
        log.info("Total time for the date is: {}\n", timeConverter.convert(totalTime));
    }
}
