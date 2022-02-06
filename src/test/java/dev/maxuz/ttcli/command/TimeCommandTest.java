package dev.maxuz.ttcli.command;

import dev.maxuz.ttcli.exception.TtRuntimeException;
import dev.maxuz.ttcli.model.Task;
import dev.maxuz.ttcli.model.TaskDay;
import dev.maxuz.ttcli.printer.Printer;
import dev.maxuz.ttcli.service.TaskDayService;
import dev.maxuz.ttcli.service.TaskService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;

import java.time.LocalDate;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class TimeCommandTest {
    private final TaskDayService taskDayService = mock(TaskDayService.class);
    private final TaskService taskService = mock(TaskService.class);
    private final Printer printer = mock(Printer.class);

    private TaskDay getTaskDay() {
        return new TaskDay(LocalDate.now());
    }

    private static Stream<Arguments> addTimeValidDataSource() {
        return Stream.of(
            Arguments.of("7s", 7000L),
            Arguments.of("59s", 59000L),
            Arguments.of("61s", 61000L),
            Arguments.of("1m", 60000L),
            Arguments.of("1m 59s", 119000L),
            Arguments.of("59m", 3540000L),
            Arguments.of("61m 12s", 3672000L),
            Arguments.of("1h", 3600000L),
            Arguments.of("1h 60s", 3660000L),
            Arguments.of("3h", 10800000L),
            Arguments.of("0s", 0L)
        );
    }

    @ParameterizedTest
    @MethodSource("addTimeValidDataSource")
    void addTime_ValidData(String time, long expected) {
        TaskDay taskDay = getTaskDay();
        when(taskDayService.getCurrentDay()).thenReturn(taskDay);

        Task task = new Task();
        task.setName("Task code");

        when(taskService.getTask(taskDay, "Task code")).thenReturn(task);

        TimeCommand command = new TimeCommand(taskDayService, taskService, printer);
        command.setName("Task code");

        command.add(time);

        ArgumentCaptor<Long> timeArgumentCaptor = ArgumentCaptor.forClass(Long.class);
        verify(taskService).addTime(eq(task), timeArgumentCaptor.capture());

        assertEquals(expected, timeArgumentCaptor.getValue());
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "", "11", "123h 11", "h", "s"
    })
    void addTime_InvalidData_ThrowsException(String time) {
        TaskDay taskDay = getTaskDay();
        when(taskDayService.getCurrentDay()).thenReturn(taskDay);

        Task task = new Task();
        task.setName("Task code");

        when(taskService.getTask(taskDay, "Task code")).thenReturn(task);

        TimeCommand command = new TimeCommand(taskDayService, taskService, printer);
        command.setName("Task code");

        assertThatThrownBy(() -> command.add(time))
            .isInstanceOf(TtRuntimeException.class)
            .hasMessage("Invalid time format. Expected example 1h 32m 11s");

        verify(taskService, times(0)).addTime(any(), anyLong());
    }

    @Test
    void addTime_TaskDoesNotExist_ThrowsException() {
        TaskDay taskDay = getTaskDay();
        when(taskDayService.getCurrentDay()).thenReturn(taskDay);

        when(taskService.getTask(eq(taskDay), any())).thenReturn(null);

        TimeCommand command = new TimeCommand(taskDayService, taskService, printer);
        command.setName("NonExistedTask");

        assertThatThrownBy(() -> command.add("10s"))
            .isInstanceOf(TtRuntimeException.class)
            .hasMessage("Task with name [NonExistedTask] is not found");

        verify(taskService, times(0)).addTime(any(), anyLong());
    }

    private static Stream<Arguments> subtractTimeValidDataSource() {
        return Stream.of(
            Arguments.of("7s", 7000L),
            Arguments.of("59s", 59000L),
            Arguments.of("61s", 61000L),
            Arguments.of("1m", 60000L),
            Arguments.of("1m 59s", 119000L),
            Arguments.of("59m", 3540000L),
            Arguments.of("61m 12s", 3672000L),
            Arguments.of("1h", 3600000L),
            Arguments.of("1h 60s", 3660000L),
            Arguments.of("3h", 10800000L),
            Arguments.of("0s", 0L)
        );
    }

    @ParameterizedTest
    @MethodSource("subtractTimeValidDataSource")
    void subtractTime_ValidData(String time, long expected) {
        TaskDay taskDay = getTaskDay();
        when(taskDayService.getCurrentDay()).thenReturn(taskDay);

        Task task = new Task();
        task.setName("Task code");

        when(taskService.getTask(taskDay, "Task code")).thenReturn(task);

        TimeCommand command = new TimeCommand(taskDayService, taskService, printer);
        command.setName("Task code");

        command.subtract(time);

        ArgumentCaptor<Long> timeArgumentCaptor = ArgumentCaptor.forClass(Long.class);
        verify(taskService).subtractTime(eq(task), timeArgumentCaptor.capture());

        assertEquals(expected, timeArgumentCaptor.getValue());
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "", "11", "123h 11", "h", "s"
    })
    void subtractTime_InvalidData_ThrowsException(String time) {
        TaskDay taskDay = getTaskDay();
        when(taskDayService.getCurrentDay()).thenReturn(taskDay);

        Task task = new Task();
        task.setName("Task code");

        when(taskService.getTask(taskDay, "Task code")).thenReturn(task);

        TimeCommand command = new TimeCommand(taskDayService, taskService, printer);
        command.setName("Task code");

        assertThatThrownBy(() -> command.subtract(time))
            .isInstanceOf(TtRuntimeException.class)
            .hasMessage("Invalid time format. Expected example 1h 32m 11s");

        verify(taskService, times(0)).subtractTime(any(), anyLong());
    }

    @Test
    void subtractTime_TaskDoesNotExist_ThrowsException() {
        TaskDay taskDay = getTaskDay();
        when(taskDayService.getCurrentDay()).thenReturn(taskDay);

        when(taskService.getTask(eq(taskDay), any())).thenReturn(null);

        TimeCommand command = new TimeCommand(taskDayService, taskService, printer);
        command.setName("NonExistedTask");

        assertThatThrownBy(() -> command.subtract("10s"))
            .isInstanceOf(TtRuntimeException.class)
            .hasMessage("Task with name [NonExistedTask] is not found");

        verify(taskService, times(0)).subtractTime(any(), anyLong());
    }

    @Test
    void addTime_DayIsNotStarted_ThrowsException() {
        when(taskDayService.getCurrentDay()).thenReturn(null);

        TimeCommand command = new TimeCommand(taskDayService, taskService, printer);
        command.setName("some-ticket");

        assertThatThrownBy(() -> command.add("10s"))
            .isInstanceOf(TtRuntimeException.class)
            .hasMessage("The day is not started");

        verify(taskService, times(0)).addTime(any(), anyLong());
    }

    @Test
    void subtractTime_DayIsNotStarted_ThrowsException() {
        when(taskDayService.getCurrentDay()).thenReturn(null);

        TimeCommand command = new TimeCommand(taskDayService, taskService, printer);
        command.setName("some-ticket");

        assertThatThrownBy(() -> command.subtract("10s"))
            .isInstanceOf(TtRuntimeException.class)
            .hasMessage("The day is not started");

        verify(taskService, times(0)).addTime(any(), anyLong());
    }

}