package dev.maxuz.ttcli.printer;

import dev.maxuz.ttcli.model.Task;
import dev.maxuz.ttcli.model.TaskDay;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Class for printing messages to a user
 */
@Slf4j
@Service
public class Printer {
    private final TimeConverter timeConverter;

    public Printer(TimeConverter timeConverter) {
        this.timeConverter = timeConverter;
    }

    public void info(String message, Object... arguments) {
        log.info(message, arguments);
    }

    public void info(TaskDay taskDay) {
        String leftAlignFormat = "| %-15s | %-11s | %-10s |";

        log.info("+-----------------+-------------+------------+");
        log.info("| Name            | State       | Time       |");
        log.info("+-----------------+-------------+------------+");

        long totalTime = 0;
        for (Task task : taskDay.getTasks()) {
            long taskTime = task.getTimeSpent();
            if (task.getStartTime() != null) {
                taskTime += Instant.now().toEpochMilli() - task.getStartTime();
            }
            log.info(String.format(leftAlignFormat, task.getName(), task.getState(), timeConverter.convert(taskTime)));
            totalTime += taskTime;
        }
        log.info("+-----------------+-------------+------------+\n");

        log.info("Today is: {}, total time is: {}\n", taskDay.getDate().format(DateTimeFormatter.ISO_DATE), timeConverter.convert(totalTime));
    }
}
