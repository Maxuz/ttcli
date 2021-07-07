package dev.maxuz.ttcli.command;

import dev.maxuz.ttcli.exception.TtRuntimeException;
import dev.maxuz.ttcli.model.Task;
import dev.maxuz.ttcli.model.TaskState;
import dev.maxuz.ttcli.service.TaskService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class StartCommandTest {
    private final TaskService taskService = mock(TaskService.class);

    @Test
    void startTask_TaskExists_StartTaskCalled() {
        Task task = new Task();
        task.setState(TaskState.WAITING);
        task.setCode("TASK_CODE");

        when(taskService.getTask("TASK_CODE"))
            .thenReturn(task);

        StartCommand command = new StartCommand(taskService);
        command.setCode(task.getCode());

        command.run();

        verify(taskService).start(task);
    }

    @Test
    void startTask_TaskDoesNotExist_ThrowsException() {
        when(taskService.getTask("TASK_CODE"))
            .thenReturn(null);

        StartCommand command = new StartCommand(taskService);
        command.setCode("TASK_CODE");

        TtRuntimeException exception = assertThrows(TtRuntimeException.class, command::run);
        assertThat(exception.getMessage()).isEqualTo("Task with code [TASK_CODE] is not found");

        verify(taskService, times(0)).start(any());
    }

    private static Stream<Arguments> stopOthersSource() {
        return Stream.of(
            Arguments.of(true),
            Arguments.of(false)
        );
    }

    @ParameterizedTest
    @MethodSource("stopOthersSource")
    void stopOthers(boolean stopOthers) {
        when(taskService.getTask("TASK_CODE"))
            .thenReturn(new Task());

        StartCommand command = new StartCommand(taskService);
        command.setCode("TASK_CODE");
        command.setStopOthers(stopOthers);

        command.run();

        if (stopOthers) {
            verify(taskService).stopAll();
        } else {
            verify(taskService, times(0)).stopAll();
        }
    }

}