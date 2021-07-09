package dev.maxuz.ttcli.command;

import dev.maxuz.ttcli.exception.TtRuntimeException;
import dev.maxuz.ttcli.model.Task;
import dev.maxuz.ttcli.model.TaskState;
import dev.maxuz.ttcli.printer.Printer;
import dev.maxuz.ttcli.service.TaskService;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Component
@Command(name = "time", description = "Set of subcommands to manipulate a task time")
public class TimeCommand implements SubCommand {

    public static final String TIME_FORMAT_ERROR_MSG = "Invalid time format. Expected example 1h 32m 11s";
    private final TaskService taskService;
    private final Printer printer;

    public TimeCommand(TaskService taskService, Printer printer) {
        this.taskService = taskService;
        this.printer = printer;
    }

    // parameters
    private String code;

    @Parameters(index = "0", description = "Code of a task to manage.")
    public void setCode(String code) {
        this.code = code;
    }

    @Command(name = "add", description = "Add time")
    public void add(
        @Parameters(paramLabel = "<time to add>",
            description = "Amount of time to add. You can specify how many hours, minutes or seconds you want to add" +
                "by passing values separated with space and specified with a time unit. Valid time units are h, m, s " +
                "(hours, minutes, seconds respectively)") String amountOfTimeToAdd) {
        long timeToAdd = getTime(amountOfTimeToAdd);
        Task task = getTask();
        taskService.addTime(task, timeToAdd);
        printer.info("Time successfully added.");
    }

    private Task getTask() {
        Task task = taskService.getTask(code);
        if (task == null) {
            throw new TtRuntimeException("Task with code [" + code + "] is not found");
        }
        return task;
    }

    private long getTime(String amountOfTimeToAdd) {
        if (amountOfTimeToAdd == null || amountOfTimeToAdd.length() == 0) {
            throw new TtRuntimeException(TIME_FORMAT_ERROR_MSG);
        }
        long timeToAdd = 0;
        for (String time : amountOfTimeToAdd.split(" ")) {
            int amount = getAmountOfTime(time);
            String timeUnit = time.substring(time.length() - 1).toLowerCase();
            switch (timeUnit) {
                case "h":
                    timeToAdd += amount * 60 * 60 * 1000L;
                    break;
                case "m":
                    timeToAdd += amount * 60 * 1000L;
                    break;
                case "s":
                    timeToAdd += amount * 1000L;
                    break;
                default:
                    throw new TtRuntimeException(TIME_FORMAT_ERROR_MSG);
            }
        }
        return timeToAdd;
    }

    private int getAmountOfTime(String time) {
        try {
            return Integer.parseInt(time.substring(0, time.length() - 1));
        } catch (NumberFormatException e) {
            throw new TtRuntimeException(TIME_FORMAT_ERROR_MSG);
        }
    }
}
