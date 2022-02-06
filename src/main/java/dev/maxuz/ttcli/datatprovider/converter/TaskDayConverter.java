package dev.maxuz.ttcli.datatprovider.converter;

import dev.maxuz.ttcli.datatprovider.dto.TaskDayTO;
import dev.maxuz.ttcli.exception.TtRuntimeException;
import dev.maxuz.ttcli.model.TaskDay;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

@Service
public class TaskDayConverter {
    private final static DateTimeFormatter DATE_PATTERN = DateTimeFormatter.ISO_DATE;
    private final TaskConverter taskConverter;

    public TaskDayConverter(TaskConverter taskConverter) {
        this.taskConverter = taskConverter;
    }

    public TaskDay convert(TaskDayTO source) {
        if (source == null) {
            return null;
        }
        if (source.getDate() == null || source.getDate().isEmpty()) {
            throw new TtRuntimeException("Task day is empty");
        }
        TaskDay taskDay = new TaskDay(LocalDate.parse(source.getDate(), DATE_PATTERN));
        if (source.getTasks() != null) {
            taskDay.setTasks(source.getTasks().stream().map(taskConverter::convert).collect(Collectors.toList()));
        }
        return taskDay;
    }

    public TaskDayTO convert(TaskDay source) {
        if (source == null) {
            return null;
        }
        TaskDayTO taskDay = new TaskDayTO();
        taskDay.setDate(source.getDate().format(DATE_PATTERN));
        if (source.getTasks() != null) {
            taskDay.setTasks(source.getTasks().stream().map(taskConverter::convert).collect(Collectors.toList()));
        }
        return taskDay;
    }
}
