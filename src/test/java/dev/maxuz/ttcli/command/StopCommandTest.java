package dev.maxuz.ttcli.command;

import dev.maxuz.ttcli.exception.TtRuntimeException;
import dev.maxuz.ttcli.model.Task;
import dev.maxuz.ttcli.service.TaskService;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StopCommandTest {
    private final TaskService taskService = mock(TaskService.class);

    @Test
    void stopTask_TaskExist_StopCalled() {
        Task task = new Task();
        task.setCode("Code 1");

        when(taskService.getTask("Code 1"))
            .thenReturn(task);

        StopCommand stopCommand = new StopCommand(taskService);
        stopCommand.setCode("Code 1");

        stopCommand.run();

        verify(taskService).stop(task);
    }

    @Test
    void stopTask_TaskDoesNotExist_ThrowsException() {
        when(taskService.getTask("TASK_CODE"))
            .thenReturn(null);

        StopCommand command = new StopCommand(taskService);
        command.setCode("TASK_CODE");

        TtRuntimeException exception = assertThrows(TtRuntimeException.class, command::run);
        assertThat(exception.getMessage()).isEqualTo("Task with code [TASK_CODE] is not found");

        verify(taskService, times(0)).stop(any());
    }


}