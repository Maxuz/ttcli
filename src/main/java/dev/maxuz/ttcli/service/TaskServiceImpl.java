package dev.maxuz.ttcli.service;

import dev.maxuz.ttcli.datatprovider.TaskDataProvider;
import dev.maxuz.ttcli.exception.TtRuntimeException;
import dev.maxuz.ttcli.model.Task;
import dev.maxuz.ttcli.model.TaskState;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class TaskServiceImpl implements TaskService {
    private final TaskDataProvider taskDataProvider;

    public TaskServiceImpl(TaskDataProvider taskDataProvider) {
        this.taskDataProvider = taskDataProvider;
    }

    @Override
    public void addTask(Task task) {
        Map<String, Task> tasks = taskDataProvider.getTasksAsMap();
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
        task.setState(TaskState.WAITING);
        taskDataProvider.saveTask(task);
    }
}
