package dev.maxuz.ttcli.command;

import dev.maxuz.ttcli.printer.Printer;
import dev.maxuz.ttcli.service.TaskService;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;

@Component
@Command(name = "list", description = "Shows information about all tasks")
public class ListCommand implements SubCommand, Runnable {

    private final TaskService taskService;
    private final Printer printer;

    public ListCommand(TaskService taskService, Printer printer) {
        this.taskService = taskService;
        this.printer = printer;
    }

    @Override
    public void run() {
        printer.info(taskService.getTasks());
    }
}
