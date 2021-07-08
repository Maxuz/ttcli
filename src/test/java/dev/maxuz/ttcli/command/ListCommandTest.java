package dev.maxuz.ttcli.command;

import dev.maxuz.ttcli.model.Task;
import dev.maxuz.ttcli.printer.Printer;
import dev.maxuz.ttcli.service.TaskService;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

class ListCommandTest {

    private final TaskService taskService = mock(TaskService.class);
    private final Printer printer = mock(Printer.class);

    @Test
    void listTasks() {
        List<Task> tasks = Arrays.asList(new Task(), new Task());
        when(taskService.getTasks()).thenReturn(tasks);

        ListCommand command = new ListCommand(taskService, printer);
        command.run();

        verify(printer).info(tasks);
    }
}