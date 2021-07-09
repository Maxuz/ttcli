package dev.maxuz.ttcli.command;

import dev.maxuz.ttcli.exception.TtRuntimeException;
import dev.maxuz.ttcli.model.Task;
import dev.maxuz.ttcli.printer.Printer;
import dev.maxuz.ttcli.service.TaskService;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Component
@Command(name = "stop", description = "Stop the task (calculate spent time and change state to WAITING)")
public class StopCommand implements SubCommand, Runnable {

    private final TaskService taskService;
    private final Printer printer;

    public StopCommand(TaskService taskService, Printer printer) {
        this.taskService = taskService;
        this.printer = printer;
    }

    // parameters
    private String code;

    @Parameters(index = "0", description = "Code of a task to stop")
    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public void run() {
        Task task = taskService.getTask(code);
        if (task == null) {
            throw new TtRuntimeException("Task with code [" + code + "] is not found");
        }
        taskService.stop(task);
        printer.info("Task {} stopped", task.getCode());
    }
}
