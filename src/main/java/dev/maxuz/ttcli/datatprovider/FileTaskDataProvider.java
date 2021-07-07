package dev.maxuz.ttcli.datatprovider;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.maxuz.ttcli.config.FileDataProviderConfig;
import dev.maxuz.ttcli.datatprovider.converter.TaskConverter;
import dev.maxuz.ttcli.datatprovider.dto.FileStorageTO;
import dev.maxuz.ttcli.datatprovider.dto.TaskStateTO;
import dev.maxuz.ttcli.datatprovider.dto.TaskTO;
import dev.maxuz.ttcli.exception.TtRuntimeException;
import dev.maxuz.ttcli.model.Task;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FileTaskDataProvider implements TaskDataProvider {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Path storage;
    private final TaskConverter taskConverter;

    public FileTaskDataProvider(FileDataProviderConfig config, TaskConverter taskConverter) {
        this.storage = config.getStorage();
        this.taskConverter = taskConverter;
    }

    @Override
    public void saveTask(Task task) {
        Map<String, TaskTO> taskMap = getTaskMap();
        taskMap.put(task.getCode(), taskConverter.convert(task));
        write(taskMap.values());
    }

    private void write(Collection<TaskTO> values) {
        FileStorageTO fileStorageTO = new FileStorageTO();
        fileStorageTO.setTasks(new ArrayList<>(values));
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(storage.toFile(), fileStorageTO);
        } catch (IOException e) {
            throw new TtRuntimeException("Write file storage error", e);
        }
    }

    private Map<String, TaskTO> getTaskMap() {
        FileStorageTO storageTO = getFileStorageTO();

        if (storageTO == null || storageTO.getTasks() == null) {
            return new HashMap<>();
        }
        return storageTO.getTasks().stream()
            .collect(Collectors.toMap(TaskTO::getCode, t -> t));
    }

    private FileStorageTO getFileStorageTO() {
        try {
            String fileContent = Files.readString(storage);
            FileStorageTO storageTO;
            if (fileContent.length() == 0) {
                storageTO = null;
            } else {
                storageTO = objectMapper.readValue(fileContent, FileStorageTO.class);
            }
            return storageTO;
        } catch (IOException e) {
            throw new TtRuntimeException("Read file storage error", e);
        }
    }

    private List<TaskTO> getTaskTOList() {
        return Optional.ofNullable(getFileStorageTO())
            .map(FileStorageTO::getTasks)
            .orElse(Collections.emptyList());
    }

    @Override
    public Task getTaskInProgress() {
        List<TaskTO> taskTOList = getTaskTOList();

        return taskTOList.stream()
            .filter(t -> t.getState() == TaskStateTO.IN_PROGRESS)
            .findFirst()
            .map(taskConverter::convert)
            .orElse(null);
    }

    @Override
    public List<Task> getTasks() {
        return getTaskTOList().stream()
            .map(taskConverter::convert)
            .collect(Collectors.toList());
    }
}
