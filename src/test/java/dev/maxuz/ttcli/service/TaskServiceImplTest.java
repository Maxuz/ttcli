package dev.maxuz.ttcli.service;

import dev.maxuz.ttcli.datatprovider.TaskDataProvider;
import dev.maxuz.ttcli.exception.TtInternalException;
import dev.maxuz.ttcli.exception.TtRuntimeException;
import dev.maxuz.ttcli.exception.TtWarningException;
import dev.maxuz.ttcli.model.Task;
import dev.maxuz.ttcli.model.TaskState;
import org.assertj.core.api.AssertionsForClassTypes;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static java.time.temporal.ChronoUnit.MINUTES;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

        when(taskDataProvider.getTasks())
            .thenReturn(Collections.singletonList(task));

        assertThatThrownBy(() -> service.addTask(task))
            .isInstanceOf(TtRuntimeException.class)
            .hasMessage("Task with code [THE_SAME_CODE] is already exists");
    }

    @Test
    void stopCurrent_CurrentExists_TaskStateUpdated() {
        Task task = new Task();
        task.setCode("Code 1");
        task.setState(TaskState.IN_PROGRESS);
        task.setTimeSpent(0);
        Instant now = Instant.now();
        task.setStartTime(now.minus(60, MINUTES).toEpochMilli());

        when(taskDataProvider.getTaskInProgress())
            .thenReturn(task);

        service.stopCurrent();

        ArgumentCaptor<Task> taskArgumentCaptor = ArgumentCaptor.forClass(Task.class);
        verify(taskDataProvider).saveTask(taskArgumentCaptor.capture());

        Task actualTask = taskArgumentCaptor.getValue();
        assertThat(actualTask.getCode()).isEqualTo("Code 1");
        assertThat(actualTask.getState()).isEqualTo(TaskState.WAITING);
        assertThat(actualTask.getStartTime()).isNull();
        assertThat(actualTask.getTimeSpent()).isCloseTo(3600000L, Offset.offset(999L));
    }

    @Test
    void stopCurrent_CurrentDoesNotExist_DoNothing() {
        when(taskDataProvider.getTaskInProgress())
            .thenReturn(null);

        service.stopCurrent();

        verify(taskDataProvider, times(0)).saveTask(any());
    }

    @Test
    void getTask() {
        Task task = new Task();
        task.setCode("Code 1");
        when(taskDataProvider.getTasks())
            .thenReturn(Collections.singletonList(task));
        assertThat(service.getTask("Code 1")).isEqualTo(task);
    }

    @Test
    void stopAll() {
        Task task = new Task();
        task.setCode("Code 1");
        task.setState(TaskState.IN_PROGRESS);
        task.setTimeSpent(0);
        Instant now = Instant.now();
        task.setStartTime(now.minus(60, MINUTES).toEpochMilli());

        when(taskDataProvider.getTasks())
            .thenReturn(Collections.singletonList(task));

        service.stop(task);

        ArgumentCaptor<Task> taskArgumentCaptor = ArgumentCaptor.forClass(Task.class);
        verify(taskDataProvider).saveTask(taskArgumentCaptor.capture());

        Task actualTask = taskArgumentCaptor.getValue();
        assertThat(actualTask.getCode()).isEqualTo("Code 1");
        assertThat(actualTask.getState()).isEqualTo(TaskState.WAITING);
        assertThat(actualTask.getStartTime()).isNull();
        assertThat(actualTask.getTimeSpent()).isCloseTo(3600000L, Offset.offset(999L));
    }

    @Test
    void startTask_StateIsWaiting_SaveCalledWithInProgressStateAndCurrentTime() {
        Task sourceTask = new Task();
        sourceTask.setCode("Code 1");
        sourceTask.setState(TaskState.WAITING);

        service.start(sourceTask);

        ArgumentCaptor<Task> taskArgumentCaptor = ArgumentCaptor.forClass(Task.class);
        verify(taskDataProvider).saveTask(taskArgumentCaptor.capture());
        Task actualTask = taskArgumentCaptor.getValue();
        assertThat(actualTask.getCode()).isEqualTo("Code 1");
        assertThat(actualTask.getState()).isEqualTo(TaskState.IN_PROGRESS);
        assertThat(actualTask.getStartTime()).isCloseTo(Instant.now().toEpochMilli(), Offset.offset(100L));
    }

    @Test
    void startTask_StateIsInProgress_ThrowsException() {
        Task sourceTask = new Task();
        sourceTask.setCode("Code 1");
        sourceTask.setState(TaskState.IN_PROGRESS);

        TtWarningException exception = assertThrows(TtWarningException.class, () -> service.start(sourceTask));
        AssertionsForClassTypes.assertThat(exception.getMessage()).isEqualTo("Task with code [Code 1] is already started");

        verify(taskDataProvider, times(0)).saveTask(any());
    }

    @Test
    void stopTask_TaskStateIsInProgressTimeSpentIsZero_SaveCalledWithCorrectValues() {
        Task task = new Task();
        task.setCode("Code 1");
        task.setState(TaskState.IN_PROGRESS);
        task.setTimeSpent(0);
        Instant now = Instant.now();
        task.setStartTime(now.minus(60, MINUTES).toEpochMilli());

        service.stop(task);

        ArgumentCaptor<Task> taskArgumentCaptor = ArgumentCaptor.forClass(Task.class);
        verify(taskDataProvider).saveTask(taskArgumentCaptor.capture());

        Task actualTask = taskArgumentCaptor.getValue();
        assertThat(actualTask.getCode()).isEqualTo("Code 1");
        assertThat(actualTask.getState()).isEqualTo(TaskState.WAITING);
        assertThat(actualTask.getStartTime()).isNull();
        assertThat(actualTask.getTimeSpent()).isCloseTo(3600000L, Offset.offset(999L));
    }

    @Test
    void stopTask_TaskStateIsInProgressTimeSpentIsNotZero_SaveCalledWithCorrectValues() {
        Task task = new Task();
        task.setCode("Code 1");
        task.setState(TaskState.IN_PROGRESS);
        task.setTimeSpent(600000);
        Instant now = Instant.now();
        task.setStartTime(now.minus(10, MINUTES).toEpochMilli());

        service.stop(task);

        ArgumentCaptor<Task> taskArgumentCaptor = ArgumentCaptor.forClass(Task.class);
        verify(taskDataProvider).saveTask(taskArgumentCaptor.capture());

        Task actualTask = taskArgumentCaptor.getValue();
        assertThat(actualTask.getCode()).isEqualTo("Code 1");
        assertThat(actualTask.getState()).isEqualTo(TaskState.WAITING);
        assertThat(actualTask.getStartTime()).isNull();
        assertThat(actualTask.getTimeSpent()).isCloseTo(1200000L, Offset.offset(999L));
    }

    @Test
    void stopTask_StateIsNotInProgress_ThrowsException() {
        Task sourceTask = new Task();
        sourceTask.setCode("Code 1");
        sourceTask.setState(TaskState.WAITING);
        sourceTask.setStartTime(Instant.now().toEpochMilli());

        TtRuntimeException exception = assertThrows(TtRuntimeException.class, () -> service.stop(sourceTask));
        AssertionsForClassTypes.assertThat(exception.getMessage()).isEqualTo("Task with code [Code 1] is not started");

        verify(taskDataProvider, times(0)).saveTask(any());
    }

    @Test
    void stopTask_StartTimeIsNull_ThrowsException() {
        Task sourceTask = new Task();
        sourceTask.setCode("Code 1");
        sourceTask.setState(TaskState.IN_PROGRESS);
        sourceTask.setStartTime(null);

        TtInternalException exception = assertThrows(TtInternalException.class, () -> service.stop(sourceTask));
        AssertionsForClassTypes.assertThat(exception.getMessage()).isEqualTo("Internal error. Start time is empty");

        verify(taskDataProvider, times(0)).saveTask(any());
    }

    @Test
    void listTasks() {
        List<Task> tasks = Arrays.asList(new Task(), new Task());
        when(taskDataProvider.getTasks())
            .thenReturn(tasks);
        assertThat(service.getTasks()).isEqualTo(tasks);
    }

    @Test
    void addTime() {
        Task task = new Task();
        task.setTimeSpent(0);

        service.addTime(task, 64000L);

        ArgumentCaptor<Task> taskArgumentCaptor = ArgumentCaptor.forClass(Task.class);
        verify(taskDataProvider).saveTask(taskArgumentCaptor.capture());

        assertThat(taskArgumentCaptor.getValue().getTimeSpent()).isEqualTo(64000L);
    }

    @Test
    void addTime_TimeSpentIsNotNull() {
        Task task = new Task();
        task.setTimeSpent(20000L);

        service.addTime(task, 64000L);

        ArgumentCaptor<Task> taskArgumentCaptor = ArgumentCaptor.forClass(Task.class);
        verify(taskDataProvider).saveTask(taskArgumentCaptor.capture());

        assertThat(taskArgumentCaptor.getValue().getTimeSpent()).isEqualTo(84000L);
    }

    private static final Instant now = Instant.now();

    private static Stream<Arguments> subtractTimeSource() {
        return Stream.of(
            Arguments.of(3600000L, null, 1200000L, 2400000L, null),
            Arguments.of(1200000L, null, 1200000L, 0L, null),
            Arguments.of(1200000L, null, 2400000L, 0L, null),
            Arguments.of(1200000L, now.minus(30, MINUTES).toEpochMilli(), 2400000L, 0L, now.minus(10, MINUTES).toEpochMilli()),
            Arguments.of(0L, now.minus(20, MINUTES).toEpochMilli(), 600000L, 0L, now.minus(10, MINUTES).toEpochMilli()),
            Arguments.of(0L, now.minus(10, MINUTES).toEpochMilli(), 900000L, 0L, now.toEpochMilli())
        );
    }

    @ParameterizedTest
    @MethodSource("subtractTimeSource")
    void subtractTime(long originalSpentTime, Long originalStartTime, long timeToSubtract, long expectedSpentTime, Long expectedStartTime) {
        Task task = new Task();
        task.setTimeSpent(originalSpentTime);
        task.setStartTime(originalStartTime);

        service.subtractTime(task, timeToSubtract);

        ArgumentCaptor<Task> taskArgumentCaptor = ArgumentCaptor.forClass(Task.class);
        verify(taskDataProvider).saveTask(taskArgumentCaptor.capture());

        Task actualTask = taskArgumentCaptor.getValue();
        assertThat(actualTask.getTimeSpent()).isEqualTo(expectedSpentTime);
        if (expectedStartTime == null) {
            assertThat(actualTask.getStartTime()).isNull();
        } else {
            assertThat(actualTask.getStartTime()).isCloseTo(expectedStartTime, Offset.offset(999L));
        }
    }
}