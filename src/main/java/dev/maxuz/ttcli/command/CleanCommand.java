package dev.maxuz.ttcli.command;

import dev.maxuz.ttcli.printer.Printer;
import dev.maxuz.ttcli.service.TaskService;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;

@Component
@Command(name = "clean", description = "Remove all tasks")
public class CleanCommand implements SubCommand, Runnable {

    private final TaskService taskService;
    private final Printer printer;

    public CleanCommand(TaskService taskService, Printer printer) {
        this.taskService = taskService;
        this.printer = printer;
    }

    @Override
    public void run() {
        printer.info("Removing all tasks. Current task list is:");
        printer.info(taskService.getTasks());
        taskService.clean();
        printer.info("Clean completed");
    }
}
