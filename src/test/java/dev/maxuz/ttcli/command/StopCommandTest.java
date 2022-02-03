package dev.maxuz.ttcli.command;

import dev.maxuz.ttcli.exception.TtRuntimeException;
import dev.maxuz.ttcli.model.Task;
import dev.maxuz.ttcli.model.TaskState;
import dev.maxuz.ttcli.printer.Printer;
import dev.maxuz.ttcli.service.TaskService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Arrays;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StopCommandTest {
    private final TaskService taskService = mock(TaskService.class);
    private final Printer printer = mock(Printer.class);

    private Task createTask(String code) {
        Task task = new Task();
        task.setName(code);
        task.setState(TaskState.IN_PROGRESS);
        return task;
    }

    @Test
    void stopTask_TaskExist_StopCalled() {
        Task task = createTask("Code 1");

        when(taskService.getTask("Code 1"))
            .thenReturn(task);

        StopCommand stopCommand = new StopCommand(taskService, printer);
        stopCommand.setName("Code 1");

        stopCommand.run();

        verify(taskService).stop(task);
        verify(printer).info("Task {} stopped", "Code 1");
    }

    @Test
    void stopTask_TaskDoesNotExist_ThrowsException() {
        when(taskService.getTask("TASK_CODE"))
            .thenReturn(null);

        StopCommand command = new StopCommand(taskService, printer);
        command.setName("TASK_CODE");

        TtRuntimeException exception = assertThrows(TtRuntimeException.class, command::run);
        assertThat(exception.getMessage()).isEqualTo("Task with name [TASK_CODE] is not found");

        verify(taskService, times(0)).stop(any());
    }

    @Test
    void stopTask_NoCode_StoppedAllTasksInProgress() {
        Task waitingTask = createTask("Code 3");
        waitingTask.setState(TaskState.WAITING);

        when(taskService.getTasks())
            .thenReturn(Arrays.asList(createTask("Code 1"), createTask("Code 2"), waitingTask));

        StopCommand stopCommand = new StopCommand(taskService, printer);
        stopCommand.run();

        ArgumentCaptor<Task> taskArgumentCaptor = ArgumentCaptor.forClass(Task.class);
        verify(taskService, times(2)).stop(taskArgumentCaptor.capture());
        assertThat(taskArgumentCaptor.getAllValues().size()).isEqualTo(2);
        assertThat(taskArgumentCaptor.getAllValues().get(0).getName()).isEqualTo("Code 1");
        assertThat(taskArgumentCaptor.getAllValues().get(1).getName()).isEqualTo("Code 2");

        ArgumentCaptor<String> taskCodeArgumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(printer, times(2)).info(eq("Task {} stopped"), taskCodeArgumentCaptor.capture());
        assertThat(taskCodeArgumentCaptor.getAllValues().get(0)).isEqualTo("Code 1");
        assertThat(taskCodeArgumentCaptor.getAllValues().get(1)).isEqualTo("Code 2");
    }
}