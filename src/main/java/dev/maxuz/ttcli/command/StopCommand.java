package dev.maxuz.ttcli.command;

import dev.maxuz.ttcli.exception.TtRuntimeException;
import dev.maxuz.ttcli.model.TaskDay;
import dev.maxuz.ttcli.printer.Printer;
import dev.maxuz.ttcli.service.TaskDayService;
import dev.maxuz.ttcli.service.TaskService;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;

@Component
@Command(name = "stop", description = "Stop all or a particular task (calculate spent time and change state to WAITING)")
public class StopCommand implements SubCommand, Runnable {

    private final TaskDayService taskDayService;
    private final TaskService taskService;
    private final Printer printer;

    public StopCommand(TaskDayService taskDayService, TaskService taskService, Printer printer) {
        this.taskDayService = taskDayService;
        this.taskService = taskService;
        this.printer = printer;
    }

    @Override
    public void run() {
        TaskDay taskDay = taskDayService.getCurrentDay();
        if (taskDay == null) {
            throw new TtRuntimeException("Day is not started");
        }
        taskService.stop(taskDay);
        taskDayService.save(taskDay);
        printer.info("All tasks were stopped");
    }
}
