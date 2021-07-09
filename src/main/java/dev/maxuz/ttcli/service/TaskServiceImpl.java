package dev.maxuz.ttcli.service;

import dev.maxuz.ttcli.datatprovider.TaskDataProvider;
import dev.maxuz.ttcli.exception.TtInternalException;
import dev.maxuz.ttcli.exception.TtRuntimeException;
import dev.maxuz.ttcli.exception.TtWarningException;
import dev.maxuz.ttcli.model.Task;
import dev.maxuz.ttcli.model.TaskState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TaskServiceImpl implements TaskService {
    private final TaskDataProvider taskDataProvider;

    public TaskServiceImpl(TaskDataProvider taskDataProvider) {
        this.taskDataProvider = taskDataProvider;
    }

    private Map<String, Task> getTasksAsMap() {
        return taskDataProvider.getTasks().stream()
            .collect(Collectors.toMap(Task::getCode, t -> t));
    }

    @Override
    public void addTask(Task task) {
        Map<String, Task> tasks = getTasksAsMap();
        if (tasks.containsKey(task.getCode())) {
            throw new TtRuntimeException("Task with code [" + task.getCode() + "] is already exists");
        }
        taskDataProvider.saveTask(task);
    }

    @Override
    public void stopCurrent() {
        Task task = taskDataProvider.getTaskInProgress();
        if (task == null) {
            return;
        }
        stop(task);
    }

    @Override
    public void stop(Task task) {
        Objects.requireNonNull(task);
        long now = Instant.now().toEpochMilli();

        if (task.getState() == TaskState.WAITING) {
            throw new TtRuntimeException("Task with code [" + task.getCode() + "] is not started");
        }
        if (task.getStartTime() == null) {
            throw new TtInternalException("Internal error. Start time is empty");
        }
        long timeSpent = task.getTimeSpent() + (now - task.getStartTime());
        task.setTimeSpent(timeSpent);
        task.setStartTime(null);
        task.setState(TaskState.WAITING);

        taskDataProvider.saveTask(task);
    }

    @Override
    public Task getTask(String code) {
        return getTasksAsMap().get(code);
    }

    @Override
    public void stopAll() {
         taskDataProvider.getTasks().stream()
         .filter(t -> t.getState() == TaskState.IN_PROGRESS)
         .forEach(this::stop);
    }

    @Override
    public void start(Task task) {
        if (task.getState() == TaskState.IN_PROGRESS) {
            throw new TtWarningException("Task with code [" + task.getCode() + "] is already started");
        }
        task.setState(TaskState.IN_PROGRESS);
        task.setStartTime(Instant.now().toEpochMilli());
        taskDataProvider.saveTask(task);
    }

    @Override
    public List<Task> getTasks() {
        return taskDataProvider.getTasks();
    }

    @Override
    public void addTime(Task task, long timeToAdd) {
        task.setTimeSpent(task.getTimeSpent() + timeToAdd);
        taskDataProvider.saveTask(task);
    }
}
