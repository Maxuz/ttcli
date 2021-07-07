package dev.maxuz.ttcli.datatprovider;

import dev.maxuz.ttcli.config.FileDataProviderConfig;
import dev.maxuz.ttcli.datatprovider.converter.TaskConverter;
import dev.maxuz.ttcli.datatprovider.dto.TaskStateTO;
import dev.maxuz.ttcli.datatprovider.dto.TaskTO;
import dev.maxuz.ttcli.model.Task;
import dev.maxuz.ttcli.model.TaskState;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.contentOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FileTaskDataProviderTest {
    private static final Path STORAGE_DIR = Paths.get("./temp-storage/");
    private static final TaskConverter taskConverter = mock(TaskConverter.class);

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

    private Path getPathFromResource(String fileName) throws URISyntaxException {
        ClassLoader classLoader = getClass().getClassLoader();
        URL resource = classLoader.getResource(fileName);
        if (resource == null) {
            throw new IllegalArgumentException("File not found! " + fileName);
        } else {
            return Paths.get(resource.toURI());
        }
    }

    @Test
    void saveTask_EmptyFile_TaskSaved() throws Exception {
        Path storage = createTempStorage();
        FileTaskDataProvider dataProvider = new FileTaskDataProvider(new FileDataProviderConfig(storage), taskConverter);

        TaskTO task = new TaskTO();
        task.setCode("NEW_TASK_CODE");
        task.setState(TaskStateTO.IN_PROGRESS);
        when(taskConverter.convert(any(Task.class)))
            .thenReturn(task);

        dataProvider.saveTask(new Task());

        String expected = contentOf(getPathFromResource("filestorage/new_task.json").toFile());
        assertThat(contentOf(storage.toFile())).isEqualTo(expected);
    }

    @Test
    void saveTask_FileIsNotEmpty_TaskSavedSourceContentKept() throws Exception {
        Path storage = createTempStorage();
        Files.writeString(storage, contentOf(getPathFromResource("filestorage/new_task.json").toFile()));
        FileTaskDataProvider dataProvider = new FileTaskDataProvider(new FileDataProviderConfig(storage), taskConverter);

        TaskTO task = new TaskTO();
        task.setCode("SECOND_TASK");
        task.setState(TaskStateTO.WAITING);
        when(taskConverter.convert(any(Task.class)))
            .thenReturn(task);

        dataProvider.saveTask(new Task());

        String expected = contentOf(getPathFromResource("filestorage/one_is_waiting_another_one_is_in_progress.json").toFile());
        assertThat(contentOf(storage.toFile())).isEqualTo(expected);
    }

    @Test
    void saveTask_UpdateTask_TaskUpdated() throws Exception {
        Path storage = createTempStorage();
        Files.writeString(storage, contentOf(getPathFromResource("filestorage/one_is_waiting_another_one_is_in_progress.json").toFile()));
        FileTaskDataProvider dataProvider = new FileTaskDataProvider(new FileDataProviderConfig(storage), taskConverter);

        TaskTO taskTO = new TaskTO();
        taskTO.setCode("NEW_TASK_CODE");
        taskTO.setState(TaskStateTO.WAITING);

        Task taskToChange = new Task();
        taskToChange.setCode("NEW_TASK_CODE");
        taskToChange.setState(TaskState.IN_PROGRESS);

        when(taskConverter.convert(any(Task.class)))
            .thenReturn(taskTO);

        dataProvider.saveTask(taskToChange);

        String expected = contentOf(getPathFromResource("filestorage/two_tasks_waiting.json").toFile());
        assertThat(contentOf(storage.toFile())).isEqualTo(expected);
    }

    @Test
    void getTaskInProgress_TaskExists_ReturnTask() throws Exception {
        Task expected = new Task();
        expected.setState(TaskState.IN_PROGRESS);
        when(taskConverter.convert(any(TaskTO.class)))
            .thenReturn(expected);

        Path storage = createTempStorage();
        Files.writeString(storage, contentOf(getPathFromResource("filestorage/one_is_waiting_another_one_is_in_progress.json").toFile()));
        FileTaskDataProvider dataProvider = new FileTaskDataProvider(new FileDataProviderConfig(storage), taskConverter);

        assertThat(dataProvider.getTaskInProgress()).isSameAs(expected);
    }

    @Test
    void getTaskInProgress_TaskDoesNotExist_ReturnNull() throws Exception {
        Path storage = createTempStorage();
        Files.writeString(storage, contentOf(getPathFromResource("filestorage/two_tasks_waiting.json").toFile()));
        FileTaskDataProvider dataProvider = new FileTaskDataProvider(new FileDataProviderConfig(storage), taskConverter);

        assertThat(dataProvider.getTaskInProgress()).isNull();
    }

    @Test
    void getTasks() throws Exception {
        Task task = new Task();
        task.setCode("NEW_TASK_CODE");
        task.setState(TaskState.WAITING);
        when(taskConverter.convert(any(TaskTO.class)))
            .thenReturn(task);

        Path storage = createTempStorage();
        Files.writeString(storage, contentOf(getPathFromResource("filestorage/new_task.json").toFile()));
        FileTaskDataProvider dataProvider = new FileTaskDataProvider(new FileDataProviderConfig(storage), taskConverter);

        assertThat(dataProvider.getTasks()).isEqualTo(Collections.singletonList(task));
    }
}