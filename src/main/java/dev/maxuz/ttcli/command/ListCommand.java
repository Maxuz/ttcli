package dev.maxuz.ttcli.command;

import dev.maxuz.ttcli.exception.TtRuntimeException;
import dev.maxuz.ttcli.model.TaskDay;
import dev.maxuz.ttcli.printer.Printer;
import dev.maxuz.ttcli.service.TaskDayService;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;

@Component
@Command(name = "list", description = "Shows information about all tasks of a day")
public class ListCommand implements SubCommand, Runnable {

    private final TaskDayService taskDayService;
    private final Printer printer;

    public ListCommand(TaskDayService taskDayService, Printer printer) {
        this.taskDayService = taskDayService;
        this.printer = printer;
    }

    @Override
    public void run() {
        TaskDay taskDay = taskDayService.getCurrentDay();
        if (taskDay == null) {
            printer.info("There is now started day - nothing to show");
            return;
        }
        printer.info(taskDay.getTasks());
    }
}
