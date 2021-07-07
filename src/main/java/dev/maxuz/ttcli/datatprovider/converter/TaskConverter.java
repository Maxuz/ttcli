package dev.maxuz.ttcli.datatprovider.converter;

import dev.maxuz.ttcli.datatprovider.dto.TaskTO;
import dev.maxuz.ttcli.model.Task;
import org.springframework.stereotype.Service;

@Service
public class TaskConverter {
    private final TaskStateConverter taskStateConverter;

    public TaskConverter(TaskStateConverter taskStateConverter) {
        this.taskStateConverter = taskStateConverter;
    }

    public TaskTO convert(Task source) {
        if (source == null) {
            return null;
        }
        TaskTO task = new TaskTO();
        task.setCode(source.getCode());
        task.setState(taskStateConverter.convert(source.getState()));
        task.setTimeSpent(source.getTimeSpent());
        task.setStartTime(source.getStartTime());
        return task;
    }

    public Task convert(TaskTO source) {
        if (source == null) {
            return null;
        }
        Task task = new Task();
        task.setCode(source.getCode());
        task.setState(taskStateConverter.convert(source.getState()));
        task.setTimeSpent(source.getTimeSpent());
        task.setStartTime(source.getStartTime());
        return task;
    }
}
