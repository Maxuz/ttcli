package dev.maxuz.ttcli.datatprovider.converter;

import dev.maxuz.ttcli.datatprovider.dto.TaskStateTO;
import dev.maxuz.ttcli.exception.TtRuntimeException;
import dev.maxuz.ttcli.model.TaskState;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.Map;

@Service
public class TaskStateConverter {
    private static final Map<TaskState, TaskStateTO> MAP = new EnumMap<>(TaskState.class);

    static {
        MAP.put(TaskState.WAITING, TaskStateTO.WAITING);
        MAP.put(TaskState.IN_PROGRESS, TaskStateTO.IN_PROGRESS);
    }

    public TaskState convert(TaskStateTO source) {
        if (source == null) {
            return null;
        }
        for (Map.Entry<TaskState, TaskStateTO> entry : MAP.entrySet()) {
            if (entry.getValue().equals(source)) {
                return entry.getKey();
            }
        }
        throw new TtRuntimeException("Task state " + source + " is not found");
    }

    public TaskStateTO convert(TaskState source) {
        if (source == null) {
            return null;
        }
        TaskStateTO taskState = MAP.get(source);
        if (taskState == null) {
            throw new TtRuntimeException("Task state " + source + " is not found");
        }
        return taskState;
    }

}
