package dev.maxuz.ttcli.command;

import dev.maxuz.ttcli.exception.TtRuntimeException;
import dev.maxuz.ttcli.model.TaskDay;
import dev.maxuz.ttcli.printer.Printer;
import dev.maxuz.ttcli.printer.TimeConverter;
import dev.maxuz.ttcli.service.TaskDayService;
import dev.maxuz.ttcli.service.TaskService;
import org.springframework.stereotype.Component;
import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@Command(name = "list", description = "Shows information about all tasks of a day")
public class ListCommand implements SubCommand, Runnable {

    private final TaskDayService taskDayService;
    private final TaskService taskService;
    private final TimeConverter timeConverter;
    private final Printer printer;

    public ListCommand(TaskDayService taskDayService, TaskService taskService, TimeConverter timeConverter, Printer printer) {
        this.taskDayService = taskDayService;
        this.taskService = taskService;
        this.timeConverter = timeConverter;
        this.printer = printer;
    }

    private LocalDate from = LocalDate.now();

    @CommandLine.Option(names = {"-from", "--from-date"}, description = "The start date (inclusive) that is used for filtering out tasks. Format yyyy-mm-dd")
    public void setFromDate(String fromStringDate) {
        this.from = parseDate(fromStringDate);
    }

    private LocalDate parseDate(String stringDate) {
        if (stringDate == null || stringDate.isEmpty()) {
            return null;
        }
        if (!stringDate.matches("\\d{4}-\\d{2}-\\d{2}")) {
            throw new TtRuntimeException("Date format is incorrect, expecting format yyyy-mm-dd");
        }

        return LocalDate.parse(stringDate);
    }

    private LocalDate to = LocalDate.now();

    @CommandLine.Option(names = {"-to", "--to-date"}, description = "The end date (inclusive) that is used for filtering out tasks. Format yyyy-mm-dd")
    public void setToDate(String toStringDate) {
        this.to = parseDate(toStringDate);
    }

    @Override
    public void run() {
        List<TaskDay> taskDays = taskDayService.find(from, to);
        if (taskDays.isEmpty()) {
            printer.info("No task days found");
            return;
        }
        taskDays.forEach(printer::info);
        if (taskDays.size() > 1) {
            printer.info("Total time for the period from {} to {} is: {}",
                from.format(DateTimeFormatter.ISO_DATE),
                to.format(DateTimeFormatter.ISO_DATE),
                countTotalTime(taskDays)
            );
        }
    }

    private String countTotalTime(List<TaskDay> taskDays) {
        long totalTime = taskDays.stream()
            .flatMap(day -> day.getTasks().stream())
            .mapToLong(taskService::countTaskTime)
            .sum();
        return timeConverter.convert(totalTime);
    }
}
