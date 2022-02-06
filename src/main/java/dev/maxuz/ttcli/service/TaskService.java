package dev.maxuz.ttcli.service;

import dev.maxuz.ttcli.exception.TtInternalException;
import dev.maxuz.ttcli.exception.TtRuntimeException;
import dev.maxuz.ttcli.model.Task;
import dev.maxuz.ttcli.model.TaskDay;
import dev.maxuz.ttcli.model.TaskState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service for handling tasks actions
 */
@Slf4j
@Service
public class TaskService {

    private Map<String, Task> getTasksAsMap(TaskDay taskDay) {
        return taskDay.getTasks().stream()
            .collect(Collectors.toMap(Task::getName, t -> t));
    }

    /**
     * Adding the task to the current list
     *
     * @param taskDay the day to add the task
     * @param task    to add
     */
    public void addTask(TaskDay taskDay, Task task) {
        if (getTasksAsMap(taskDay).containsKey(task.getName())) {
            throw new TtRuntimeException("Task with name [" + task.getName() + "] is already exists");
        }
        taskDay.addTask(task);
    }

    /**
     * Stop all tasks in the day
     *
     * @param taskDay day for stopping tasks
     */
    public void stop(TaskDay taskDay) {
        for (Task task : taskDay.getTasks()) {
            long now = Instant.now().toEpochMilli();

            if (task.getState() != TaskState.IN_PROGRESS) {
                continue;
            }

            if (task.getStartTime() == null) {
                throw new TtInternalException("Internal error. Start time is empty");
            }
            long timeSpent = task.getTimeSpent() + (now - task.getStartTime());
            task.setTimeSpent(timeSpent);
            task.setStartTime(null);
            task.setState(TaskState.WAITING);
        }
    }

    /**
     * Find a task by name
     *
     * @param taskDay the day to find the task
     * @param name    of the task
     * @return task, or null
     */
    public Task getTask(TaskDay taskDay, String name) {
        if (StringUtils.isEmpty(name)) {
            throw new TtRuntimeException("The task name can't be empty");
        }
        Map<String, Task> tasks = getTasksAsMap(taskDay);
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

    /**
     * Starts the task
     *
     * @param task to start
     */
    public void start(Task task) {
        if (task.getState() == TaskState.IN_PROGRESS) {
            throw new TtRuntimeException("Task with name [" + task.getName() + "] is already started");
        }
        task.setState(TaskState.IN_PROGRESS);
        task.setStartTime(Instant.now().toEpochMilli());
    }

    /**
     * Added time to the task
     *
     * @param task to add time
     * @param time to add in milliseconds
     */
    public void addTime(Task task, long time) {
        task.setTimeSpent(task.getTimeSpent() + time);
    }

    /**
     * Subtract time from the task
     *
     * @param task to subtract time
     * @param time to subtract
     */
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
    }
}
