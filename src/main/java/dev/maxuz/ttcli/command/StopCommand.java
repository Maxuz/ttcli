package dev.maxuz.ttcli.command;

import dev.maxuz.ttcli.exception.TtRuntimeException;
import dev.maxuz.ttcli.model.Task;
import dev.maxuz.ttcli.model.TaskState;
import dev.maxuz.ttcli.printer.Printer;
import dev.maxuz.ttcli.service.TaskService;
import org.springframework.stereotype.Component;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Component
@Command(name = "stop", description = "Stop all or a particular task (calculate spent time and change state to WAITING)")
public class StopCommand implements SubCommand, Runnable {

    private final TaskService taskService;
    private final Printer printer;

    public StopCommand(TaskService taskService, Printer printer) {
        this.taskService = taskService;
        this.printer = printer;
    }

    // parameters
    private String code;

    @CommandLine.Option(names = { "-t", "--task-code" }, paramLabel = "TASK_CODE", description = "the task code to stop")
    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public void run() {
        if (code == null || code.length() == 0) {
            printer.info("Stopping all tasks");
            for (Task task : taskService.getTasks()) {
                if (task.getState() != TaskState.IN_PROGRESS) {
                    continue;
                }
                taskService.stop(task);
                printer.info("Task {} stopped", task.getCode());
            }
        } else {
            Task task = taskService.getTask(code);
            if (task == null) {
                throw new TtRuntimeException("Task with code [" + code + "] is not found");
            }
            taskService.stop(task);
            printer.info("Task {} stopped", task.getCode());
        }
    }
}
