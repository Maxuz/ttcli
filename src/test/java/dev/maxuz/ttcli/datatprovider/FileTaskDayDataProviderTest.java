package dev.maxuz.ttcli.datatprovider;

import dev.maxuz.ttcli.config.FileDataProviderConfig;
import dev.maxuz.ttcli.datatprovider.converter.TaskDayConverter;
import dev.maxuz.ttcli.datatprovider.dto.TaskDayTO;
import dev.maxuz.ttcli.datatprovider.dto.TaskStateTO;
import dev.maxuz.ttcli.datatprovider.dto.TaskTO;
import dev.maxuz.ttcli.model.TaskDay;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.contentOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FileTaskDayDataProviderTest {
    private static final Path STORAGE_DIR = Paths.get("./temp-storage/");
    private static final TaskDayConverter taskDayConverter = mock(TaskDayConverter.class);

    private void write(Path path, String sourceFilePath) throws URISyntaxException, IOException {
        Files.write(path, Files.readAllLines(getPathFromResource(sourceFilePath)));
    }

    private Path getPathFromResource(String fileName) throws URISyntaxException {
        ClassLoader classLoader = getClass().getClassLoader();
        URL resource = classLoader.getResource(fileName);
        if (resource == null) {
            throw new IllegalArgumentException("File not found! " + fileName);
        } else {
            return Paths.get(resource.toURI());
        }
    }

    @BeforeAll
    static void beforeAll() throws IOException {
        if (Files.exists(STORAGE_DIR)) {
            clearDir();
        }
        Files.createDirectories(STORAGE_DIR);
    }

    @AfterAll
    static void afterAll() throws IOException {
        clearDir();
        Files.deleteIfExists(STORAGE_DIR);
    }

    private static void clearDir() throws IOException {
        try (Stream<Path> stream = Files.list(STORAGE_DIR)) {
            stream.forEach(file -> {
                try {
                    Files.deleteIfExists(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    private Path createTempStorage() {
        try {
            return Files.createTempFile(STORAGE_DIR, "", ".tt");
        } catch (IOException e) {
            throw new RuntimeException("Create temp storage error", e);
        }
    }

    @Test
    void saveTaskDay_EmptyFile_TaskDayIsSaved() throws Exception {
        Path storage = createTempStorage();
        FileTaskDayDataProvider dataProvider = new FileTaskDayDataProvider(new FileDataProviderConfig(storage), taskDayConverter);

        TaskDayTO day = new TaskDayTO();
        day.setDate("2022-02-05");
        TaskTO task = new TaskTO();
        task.setName("NEW_TASK_CODE");
        task.setState(TaskStateTO.IN_PROGRESS);
        day.setTasks(Collections.singletonList(task));

        when(taskDayConverter.convert(any(TaskDay.class)))
            .thenReturn(day);

        dataProvider.save(new TaskDay(LocalDate.parse("2022-02-05")));

        String expected = contentOf(getPathFromResource("filestorage/single-day-single-task.json").toFile());
        assertThat(contentOf(storage.toFile())).isEqualTo(expected);
    }

    @Test
    void saveTaskDay_FileIsNotEmpty_TaskDayIsSavedSourceContentIsKept() throws Exception {
        Path storage = createTempStorage();

        write(storage, "filestorage/single-day-single-task.json");
        FileTaskDayDataProvider dataProvider = new FileTaskDayDataProvider(new FileDataProviderConfig(storage), taskDayConverter);

        TaskDayTO day = new TaskDayTO();
        day.setDate("2022-02-06");
        TaskTO task = new TaskTO();
        task.setName("SECOND_TASK");
        task.setState(TaskStateTO.WAITING);
        day.setTasks(Collections.singletonList(task));

        when(taskDayConverter.convert(any(TaskDay.class)))
            .thenReturn(day);

        dataProvider.save(new TaskDay(LocalDate.parse("2022-02-06")));

        String expected = contentOf(getPathFromResource("filestorage/two-days-with-one-task-in-each.json").toFile());
        assertThat(contentOf(storage.toFile())).isEqualTo(expected);
    }

    @Test
    void saveTask_UpdateTask_TaskUpdated() throws Exception {
        Path storage = createTempStorage();
        write(storage, "filestorage/two-days-with-one-task-in-each.json");
        FileTaskDayDataProvider dataProvider = new FileTaskDayDataProvider(new FileDataProviderConfig(storage), taskDayConverter);

        TaskDayTO toChange = new TaskDayTO();
        toChange.setDate("2022-02-05");
        TaskTO taskTO = new TaskTO();
        taskTO.setName("NEW_TASK_CODE");
        taskTO.setState(TaskStateTO.WAITING);
        toChange.setTasks(Collections.singletonList(taskTO));

        when(taskDayConverter.convert(any(TaskDay.class)))
            .thenReturn(toChange);

        dataProvider.save(new TaskDay(LocalDate.parse("2022-02-05")));

        String expected = contentOf(getPathFromResource("filestorage/two-days-with-waiting-tasks-in-each.json").toFile());
        assertThat(contentOf(storage.toFile())).isEqualTo(expected);
    }

    @Test
    void getTasks() throws Exception {
        Path storage = createTempStorage();
        write(storage, "filestorage/single-day-single-task.json");
        FileTaskDayDataProvider dataProvider = new FileTaskDayDataProvider(new FileDataProviderConfig(storage), taskDayConverter);

        TaskDay task = new TaskDay(LocalDate.now());
        when(taskDayConverter.convert(any(TaskDayTO.class)))
            .thenReturn(task);

        List<TaskDay> days = dataProvider.findAll();
        assertThat(days).isNotNull();
        assertThat(days.size()).isEqualTo(1);
        assertThat(days.get(0)).isEqualTo(task);
    }
}