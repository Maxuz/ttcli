package dev.maxuz.ttcli.command;

import dev.maxuz.ttcli.exception.TtRuntimeException;
import dev.maxuz.ttcli.model.Task;
import dev.maxuz.ttcli.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Slf4j
@Component
@Command(name = "start", description = "Setting the start time and change status to the IN_PROGRESS")
public class StartCommand implements SubCommand {

    private final TaskService taskService;

    public StartCommand(TaskService taskService) {
        this.taskService = taskService;
    }

    // parameters
    private String code;

    @Parameters(index = "0", description = "Code of a task to start")
    public void setCode(String code) {
        this.code = code;
    }

    // options
    private boolean stopOthers = false;

    @Option(names = { "-s", "--stop-others" }, description = "Stops all other tasks")
    public void setStopOthers(boolean stopOthers) {
        this.stopOthers = stopOthers;
    }

    @Override
    public void run() {
        Task task = taskService.getTask(code);
        if (task == null) {
            throw new TtRuntimeException("Task with code [" + code + "] is not found");
        }

        if (stopOthers) {
            taskService.stopAll();
            log.info("All tasks successfully stopped");
        }
        taskService.start(task);
        log.info("Task {} successfully started", task.getCode());
    }
}
