package dev.maxuz.ttcli.command;

import dev.maxuz.ttcli.exception.TtRuntimeException;
import dev.maxuz.ttcli.model.Task;
import dev.maxuz.ttcli.printer.Printer;
import dev.maxuz.ttcli.service.TaskService;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class TimeCommandTest {
    private final TaskService taskService = mock(TaskService.class);
    private final Printer printer = mock(Printer.class);

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
        Task task = new Task();
        task.setCode("Task code");

        when(taskService.getTask("Task code")).thenReturn(task);

        TimeCommand command = new TimeCommand(taskService, printer);
        command.setCode("Task code");

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
        Task task = new Task();
        task.setCode("Task code");

        when(taskService.getTask("Task code")).thenReturn(task);

        TimeCommand command = new TimeCommand(taskService, printer);
        command.setCode("Task code");

        TtRuntimeException exception = assertThrows(TtRuntimeException.class, () -> command.add(time));
        assertEquals("Invalid time format. Expected example 1h 32m 11s", exception.getMessage());

        verify(taskService, times(0)).addTime(any(), anyLong());
    }
}