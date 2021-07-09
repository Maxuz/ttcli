package dev.maxuz.ttcli.printer;

import dev.maxuz.ttcli.model.Task;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

/**
 * Class for printing message to a user
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

    public void info(List<Task> tasks) {
        long totalTime = 0;
        for (Task task : tasks) {
            long taskTime = task.getTimeSpent();
            if (task.getStartTime() != null) {
                taskTime += Instant.now().toEpochMilli() - task.getStartTime();
            }
            log.info("{}\tstate: {}\ttime: {}", task.getCode(), task.getState(), timeConverter.convert(taskTime));
            totalTime += taskTime;
        }
        log.info("\n=================");
        log.info("Total time: {}", timeConverter.convert(totalTime));
    }
}
