package dev.maxuz.ttcli.service;

import dev.maxuz.ttcli.exception.TtRuntimeException;
import dev.maxuz.ttcli.model.Task;
import dev.maxuz.ttcli.model.TaskDay;
import dev.maxuz.ttcli.model.TaskState;
import org.assertj.core.api.AssertionsForClassTypes;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static java.time.temporal.ChronoUnit.MINUTES;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TaskServiceTest {
    private final TaskService service = new TaskService();

    private static Task createTask(String name) {
        Task task = new Task();
        task.setName(name);
        return task;
    }

    private static Task createTask(long spentTime, Long startTime) {
        Task task = new Task();
        task.setTimeSpent(spentTime);
        task.setStartTime(startTime);
        return task;
    }

    private static TaskDay createTaskDay() {
        return new TaskDay(LocalDate.now());
    }

    @Test
    void addTask() {
        TaskDay taskDay = createTaskDay();
        Task task = new Task();
        service.addTask(taskDay, task);
        assertThat(taskDay.getTasks().contains(task)).isTrue();
    }

    @Test
    void addTask_TaskAlreadyExists_ThrowsException() {
        TaskDay taskDay = createTaskDay();
        Task task = createTask("THE_SAME_CODE");
        taskDay.addTask(task);

        assertThatThrownBy(() -> service.addTask(taskDay, task))
            .isInstanceOf(TtRuntimeException.class)
            .hasMessage("Task with name [THE_SAME_CODE] is already exists");
    }

    private static Stream<Arguments> getTaskSource_Positive() {
        return Stream.of(
            Arguments.of(Collections.singletonList(createTask("A-1")), "A-1", createTask("A-1")),
            Arguments.of(Collections.singletonList(createTask("A-2")), "A-1", null),
            Arguments.of(Arrays.asList(createTask("A-1"), createTask("A-2")), "A-1", createTask("A-1")),
            Arguments.of(Arrays.asList(createTask("A-1"), createTask("A-2")), "a-1", createTask("A-1")),
            Arguments.of(Arrays.asList(createTask("A-1"), createTask("A-12")), "A-1", createTask("A-1")),
            Arguments.of(Arrays.asList(createTask("A-1"), createTask("BA-1")), "A-1", createTask("A-1")),

            Arguments.of(Arrays.asList(createTask("A-1"), createTask("B-2")), "1", createTask("A-1")),
            Arguments.of(Arrays.asList(createTask("A-1"), createTask("B-2")), "A", createTask("A-1")),
            Arguments.of(Arrays.asList(createTask("A-1"), createTask("B-2")), "a", createTask("A-1")),
            Arguments.of(Arrays.asList(createTask("A-1"), createTask("B-2")), "A-", createTask("A-1")),
            Arguments.of(Arrays.asList(createTask("A-1"), createTask("B-2")), "-1", createTask("A-1")),

            Arguments.of(Collections.emptyList(), "A-1", null)
        );
    }

    @ParameterizedTest
    @MethodSource("getTaskSource_Positive")
    void getTask_Positive(List<Task> tasks, String name, Task expected) {
        TaskDay taskDay = createTaskDay();
        tasks.forEach(taskDay::addTask);

        if (expected == null) {
            assertThat(service.getTask(taskDay, name)).isNull();
        } else {
            assertThat(service.getTask(taskDay, name)).isEqualTo(expected);
        }
    }

    @Test
    void getTask_MultipleTaskForTheCode_ThrowsException() {
        TaskDay taskDay = createTaskDay();
        taskDay.addTask(createTask("A-1"));
        taskDay.addTask(createTask("A-2"));

        TtRuntimeException exception = assertThrows(TtRuntimeException.class, () -> service.getTask(taskDay, "A"));
        assertThat(exception.getMessage()).isEqualTo("Found more than one task for name [A]");
    }

    @Test
    void getTask_CodeIsNull_ThrowsException() {
        TaskDay taskDay = createTaskDay();
        taskDay.addTask(createTask("A-1"));
        taskDay.addTask(createTask("A-2"));

        TtRuntimeException exception = assertThrows(TtRuntimeException.class, () -> service.getTask(taskDay, null));
        assertThat(exception.getMessage()).isEqualTo("The task name can't be empty");
    }

    @Test
    void getTask_CodeIsEmptyString_ThrowsException() {
        TaskDay taskDay = createTaskDay();
        taskDay.addTask(createTask("A-1"));
        taskDay.addTask(createTask("A-2"));

        TtRuntimeException exception = assertThrows(TtRuntimeException.class, () -> service.getTask(taskDay, null));
        assertThat(exception.getMessage()).isEqualTo("The task name can't be empty");
    }

    @Test
    void stop() {
        Task task = createTask("Code 1");
        task.setState(TaskState.IN_PROGRESS);
        task.setTimeSpent(0);
        Instant now = Instant.now();
        task.setStartTime(now.minus(60, MINUTES).toEpochMilli());

        TaskDay taskDay = createTaskDay();
        taskDay.addTask(task);

        service.stop(taskDay);

        assertThat(taskDay.getTasks().size()).isEqualTo(1);
        Task actualTask = taskDay.getTasks().get(0);
        assertThat(actualTask.getName()).isEqualTo("Code 1");
        assertThat(actualTask.getState()).isEqualTo(TaskState.WAITING);
        assertThat(actualTask.getStartTime()).isNull();
        assertThat(actualTask.getTimeSpent()).isCloseTo(3600000L, Offset.offset(999L));
    }

    @Test
    void startTask_StateIsWaiting_SaveCalledWithInProgressStateAndCurrentTime() {
        Task task = createTask("Code 1");
        task.setState(TaskState.WAITING);

        service.start(task);

        assertThat(task.getName()).isEqualTo("Code 1");
        assertThat(task.getState()).isEqualTo(TaskState.IN_PROGRESS);
        assertThat(task.getStartTime()).isCloseTo(Instant.now().toEpochMilli(), Offset.offset(100L));
    }

    @Test
    void startTask_StateIsInProgress_ThrowsException() {
        Task sourceTask = createTask("Code 1");
        sourceTask.setState(TaskState.IN_PROGRESS);

        AssertionsForClassTypes.assertThatThrownBy(() -> service.start(sourceTask))
            .isInstanceOf(TtRuntimeException.class)
            .hasMessageContaining("Task with name [Code 1] is already started");
    }

    @Test
    void stopTask_TaskStateIsInProgressTimeSpentIsZero_SaveCalledWithCorrectValues() {
        Task task = createTask("Code 1");
        task.setState(TaskState.IN_PROGRESS);
        task.setTimeSpent(0);
        Instant now = Instant.now();
        task.setStartTime(now.minus(60, MINUTES).toEpochMilli());

        TaskDay taskDay = createTaskDay();
        taskDay.addTask(task);

        service.stop(taskDay);

        assertThat(taskDay.getTasks().size()).isEqualTo(1);
        Task actualTask = taskDay.getTasks().get(0);
        assertThat(actualTask.getName()).isEqualTo("Code 1");
        assertThat(actualTask.getState()).isEqualTo(TaskState.WAITING);
        assertThat(actualTask.getStartTime()).isNull();
        assertThat(actualTask.getTimeSpent()).isCloseTo(3600000L, Offset.offset(999L));
    }

    @Test
    void stopTask_TaskStateIsInProgressTimeSpentIsNotZero_SaveCalledWithCorrectValues() {
        Task task = createTask("Code 1");
        task.setState(TaskState.IN_PROGRESS);
        task.setTimeSpent(600000);
        Instant now = Instant.now();
        task.setStartTime(now.minus(10, MINUTES).toEpochMilli());

        TaskDay taskDay = createTaskDay();
        taskDay.addTask(task);

        service.stop(taskDay);

        assertThat(taskDay.getTasks().size()).isEqualTo(1);
        Task actualTask = taskDay.getTasks().get(0);
        assertThat(actualTask.getName()).isEqualTo("Code 1");
        assertThat(actualTask.getState()).isEqualTo(TaskState.WAITING);
        assertThat(actualTask.getStartTime()).isNull();
        assertThat(actualTask.getTimeSpent()).isCloseTo(1200000L, Offset.offset(999L));
    }

    @Test
    void stopTask_StartTimeIsNull_ThrowsException() {
        Task sourceTask = createTask("Code 1");
        sourceTask.setState(TaskState.IN_PROGRESS);
        sourceTask.setStartTime(null);

        TaskDay taskDay = createTaskDay();
        taskDay.addTask(sourceTask);

        AssertionsForClassTypes.assertThatThrownBy(() -> service.stop(taskDay))
            .isInstanceOf(TtRuntimeException.class)
            .hasMessageContaining("Internal error. Start time is empty");
    }

    @Test
    void addTime() {
        Task task = new Task();
        task.setTimeSpent(0);

        service.addTime(task, 64000L);

        assertThat(task.getTimeSpent()).isEqualTo(64000L);
    }

    @Test
    void addTime_TimeSpentIsNotNull() {
        Task task = new Task();
        task.setTimeSpent(20000L);

        service.addTime(task, 64000L);

        assertThat(task.getTimeSpent()).isEqualTo(84000L);
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

        assertThat(task.getTimeSpent()).isEqualTo(expectedSpentTime);
        if (expectedStartTime == null) {
            assertThat(task.getStartTime()).isNull();
        } else {
            assertThat(task.getStartTime()).isCloseTo(expectedStartTime, Offset.offset(999L));
        }
    }

    private static Stream<Arguments> countTaskTimeSource() {
        return Stream.of(
            Arguments.of(createTask(0, null), 0L),
            Arguments.of(createTask(0, Instant.now().minusMillis(100).toEpochMilli()), 100),
            Arguments.of(createTask(123, null), 123),
            Arguments.of(createTask(100, Instant.now().minusMillis(100).toEpochMilli()), 200)
        );
    }

    @ParameterizedTest
    @MethodSource("countTaskTimeSource")
    void countTaskTime(Task task, long expected) {
        Long actual = service.countTaskTime(task);
        assertThat(actual).isCloseTo(expected, Offset.offset(200L));
    }
}