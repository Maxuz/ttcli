package dev.maxuz.ttcli.service;

import dev.maxuz.ttcli.datatprovider.TaskDataProvider;
import dev.maxuz.ttcli.exception.TtInternalException;
import dev.maxuz.ttcli.exception.TtRuntimeException;
import dev.maxuz.ttcli.exception.TtWarningException;
import dev.maxuz.ttcli.model.Task;
import dev.maxuz.ttcli.model.TaskState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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
            .collect(Collectors.toMap(Task::getName, t -> t));
    }

    @Override
    public void addTask(Task task) {
        Map<String, Task> tasks = getTasksAsMap();
        if (tasks.containsKey(task.getName())) {
            throw new TtRuntimeException("Task with name [" + task.getName() + "] is already exists");
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
            throw new TtRuntimeException("Task with name [" + task.getName() + "] is not started");
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
    public Task getTask(String name) {
        if (StringUtils.isEmpty(name)) {
            throw new TtRuntimeException("The task name can't be empty");
        }
        Map<String, Task> tasks = getTasksAsMap();
        if (tasks.containsKey(name)) {
            return tasks.get(name);
        }
        if (tasks.containsKey(name.toLowerCase())) {
            return tasks.get(name.toLowerCase());
        }

        if (tasks.containsKey(name.toUpperCase())) {
            return tasks.get(name.toUpperCase());
        }
        List<String> foundTasks = tasks.keySet().stream()
            .filter(tName -> tName.toLowerCase().contains(name.toLowerCase()))
            .collect(Collectors.toList());
        if (foundTasks.isEmpty()) {
            return null;
        }
        if (foundTasks.size() == 1) {
            return tasks.get(foundTasks.get(0));
        } else {
            throw new TtRuntimeException("Found more than one task for name [" + name + "]");
        }
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
            throw new TtWarningException("Task with name [" + task.getName() + "] is already started");
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
    public void addTime(Task task, long time) {
        task.setTimeSpent(task.getTimeSpent() + time);
        taskDataProvider.saveTask(task);
    }

    @Override
    public void subtractTime(Task task, long time) {
        long timeSpent = task.getTimeSpent();
        Long startTime = task.getStartTime();
        if (timeSpent >= time) {
            task.setTimeSpent(timeSpent - time);
        } else {
            if (startTime != null) {
                long now = Instant.now().toEpochMilli();
                task.setStartTime(Math.min((startTime + (time - timeSpent)), now));
            }
            task.setTimeSpent(0L);
        }
        taskDataProvider.saveTask(task);
    }
}
