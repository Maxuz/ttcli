package dev.maxuz.ttcli.service;

import dev.maxuz.ttcli.datatprovider.TaskDataProvider;
import dev.maxuz.ttcli.exception.TtRuntimeException;
import dev.maxuz.ttcli.model.Task;
import dev.maxuz.ttcli.model.TaskState;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class TaskServiceImplTest {
    private final TaskDataProvider taskDataProvider = mock(TaskDataProvider.class);

    private final TaskServiceImpl service = new TaskServiceImpl(taskDataProvider);

    @Test
    void addTask() {
        Task task = new Task();
        service.addTask(task);
        verify(taskDataProvider).saveTask(task);
    }

    @Test
    void addTask_TaskAlreadyExists_ThrowsException() {
        Task task = new Task();
        task.setCode("THE_SAME_CODE");

        when(taskDataProvider.getTasksAsMap())
            .thenReturn(Collections.singletonMap(task.getCode(), task));

        assertThatThrownBy(() -> service.addTask(task))
            .isInstanceOf(TtRuntimeException.class)
            .hasMessage("Task with code [THE_SAME_CODE] is already exists");
    }

    @Test
    void stopCurrent_CurrentExists_TaskStateUpdated() {
        Task task = new Task();
        task.setState(TaskState.IN_PROGRESS);
        when(taskDataProvider.getTaskInProgress())
            .thenReturn(task);

        service.stopCurrent();

        ArgumentCaptor<Task> taskArgumentCaptor = ArgumentCaptor.forClass(Task.class);
        verify(taskDataProvider).saveTask(taskArgumentCaptor.capture());

        assertThat(taskArgumentCaptor.getValue().getState()).isEqualTo(TaskState.WAITING);
    }

    @Test
    void stopCurrent_CurrentDoesNotExist_DoNothing() {
        Task task = new Task();
        task.setState(TaskState.IN_PROGRESS);
        when(taskDataProvider.getTaskInProgress())
            .thenReturn(null);

        service.stopCurrent();

        verify(taskDataProvider, times(0)).saveTask(any());
    }
}