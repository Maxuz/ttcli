package dev.maxuz.ttcli.service;

import dev.maxuz.ttcli.datatprovider.TaskDayDataProvider;
import dev.maxuz.ttcli.exception.TtRuntimeException;
import dev.maxuz.ttcli.model.TaskDay;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

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

    public void save(TaskDay taskDay) {
        if (taskDay == null) {
            throw new TtRuntimeException("Task day can't be null");
        }
        taskDayDataProvider.save(taskDay);
    }
}
