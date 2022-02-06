package dev.maxuz.ttcli.datatprovider;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.maxuz.ttcli.config.FileDataProviderConfig;
import dev.maxuz.ttcli.datatprovider.converter.TaskDayConverter;
import dev.maxuz.ttcli.datatprovider.dto.FileStorageTO;
import dev.maxuz.ttcli.datatprovider.dto.TaskDayTO;
import dev.maxuz.ttcli.exception.TtRuntimeException;
import dev.maxuz.ttcli.model.TaskDay;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FileTaskDayDataProvider implements TaskDayDataProvider {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Path storage;
    private final TaskDayConverter taskDayConverter;

    public FileTaskDayDataProvider(FileDataProviderConfig config, TaskDayConverter taskDayConverter) {
        this.storage = config.getStorage();
        this.taskDayConverter = taskDayConverter;
    }

    @Override
    public void save(TaskDay taskDay) {
        Map<String, TaskDayTO> taskMap = getTaskDayMap();
        TaskDayTO taskDayTo = taskDayConverter.convert(taskDay);
        taskMap.put(taskDayTo.getDate(), taskDayTo);
        write(taskMap.values().stream().sorted(Comparator.comparing(TaskDayTO::getDate).reversed()).collect(Collectors.toList()));
    }

    @Override
    public List<TaskDay> findAll() {
        return getTaskDayTOList().stream()
            .map(taskDayConverter::convert)
            .sorted(Comparator.comparing(TaskDay::getDate).reversed())
            .collect(Collectors.toList());
    }

    private void write(Collection<TaskDayTO> values) {
        FileStorageTO fileStorageTO = new FileStorageTO();
        fileStorageTO.setDays(new ArrayList<>(values));
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(storage.toFile(), fileStorageTO);
        } catch (IOException e) {
            throw new TtRuntimeException("Write file storage error", e);
        }
    }

    private Map<String, TaskDayTO> getTaskDayMap() {
        FileStorageTO storageTO = getFileStorageTO();

        if (storageTO == null || storageTO.getDays() == null) {
            return new HashMap<>();
        }
        return storageTO.getDays().stream()
            .collect(Collectors.toMap(TaskDayTO::getDate, d -> d));
    }

    private FileStorageTO getFileStorageTO() {
        try {
            List<String> fileContent = Files.readAllLines(storage);
            FileStorageTO storageTO;
            if (fileContent.isEmpty()) {
                storageTO = null;
            } else {
                storageTO = objectMapper.readValue(String.join("\n", fileContent), FileStorageTO.class);
            }
            return storageTO;
        } catch (IOException e) {
            throw new TtRuntimeException("Read file storage error", e);
        }
    }

    private List<TaskDayTO> getTaskDayTOList() {
        return Optional.ofNullable(getFileStorageTO())
            .map(FileStorageTO::getDays)
            .orElse(Collections.emptyList());
    }
}
