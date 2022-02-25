package dev.maxuz.ttcli.service;

import dev.maxuz.ttcli.datatprovider.TaskDayDataProvider;
import dev.maxuz.ttcli.exception.TtRuntimeException;
import dev.maxuz.ttcli.model.TaskDay;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskDayService {
    private final TaskDayDataProvider taskDayDataProvider;

    public TaskDayService(TaskDayDataProvider taskDayDataProvider) {
        this.taskDayDataProvider = taskDayDataProvider;
    }

    public TaskDay getCurrentDay() {
        List<TaskDay> days = taskDayDataProvider.findAll();
        if (days == null || days.isEmpty()) {
            return null;
        }
        TaskDay currentDay = days.get(0);
        if (currentDay != null) {
            LocalDate now = LocalDate.now();
            if (currentDay.getDate().equals(now)) {
                return currentDay;
            } else if (currentDay.getDate().isAfter(now)) {
                throw new TtRuntimeException("Illegal state - current date can't be in the future");
            } else if (currentDay.getDate().isBefore(now)) {
                return null;
            } else {
                throw new TtRuntimeException("Unhandled state: " + currentDay);
            }
        }
        return null;
    }

    public TaskDay getLastDay() {
        List<TaskDay> days = taskDayDataProvider.findAll();
        if (days == null || days.isEmpty()) {
            return null;
        }
        return days.get(0);
    }

    public void save(TaskDay taskDay) {
        if (taskDay == null) {
            throw new TtRuntimeException("Task day can't be null");
        }
        taskDayDataProvider.save(taskDay);
    }

    public List<TaskDay> find(LocalDate fromInclusive, LocalDate toInclusive) {
        if (toInclusive.isBefore(fromInclusive)) {
            throw new TtRuntimeException("To date can't be before from date");
        }
        final LocalDate fromExclusive = fromInclusive.minusDays(1);
        final LocalDate toExclusive = toInclusive.plusDays(1);
        return taskDayDataProvider.findAll().stream()
            .filter(day -> day.getDate().isAfter(fromExclusive) && day.getDate().isBefore(toExclusive))
            .collect(Collectors.toList());
    }
}
