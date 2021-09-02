package dev.maxuz.ttcli.command;

import dev.maxuz.ttcli.model.Task;
import dev.maxuz.ttcli.model.TaskState;
import dev.maxuz.ttcli.printer.Printer;
import dev.maxuz.ttcli.service.TaskService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class AddCommandTest {
    private final TaskService taskService = mock(TaskService.class);
    private final Printer printer = mock(Printer.class);

    @Test
    void addTask() {
        AddCommand command = new AddCommand(taskService, printer);
        command.setCode("TASK_CODE");

        command.run();

        ArgumentCaptor<Task> taskArgumentCaptor = ArgumentCaptor.forClass(Task.class);
        verify(taskService).addTask(taskArgumentCaptor.capture());
        Task task = taskArgumentCaptor.getValue();
        assertThat(task.getCode()).isEqualTo("TASK_CODE");
        assertThat(task.getState()).isEqualTo(TaskState.WAITING);
    }

    @Test
    void addTask_StartImmediatelyIsTrue() {
        AddCommand command = new AddCommand(taskService, printer);
        command.setCode("TASK_CODE");
        command.setStartImmediately(true);

        command.run();

        ArgumentCaptor<Task> taskArgumentCaptor = ArgumentCaptor.forClass(Task.class);
        verify(taskService).addTask(taskArgumentCaptor.capture());
        Task task = taskArgumentCaptor.getValue();
        verify(taskService).stopCurrent();
        verify(taskService).start(task);
        verify(printer).info("Task {} added", "TASK_CODE");
    }
}