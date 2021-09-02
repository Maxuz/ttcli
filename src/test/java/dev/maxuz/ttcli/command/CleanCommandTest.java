package dev.maxuz.ttcli.command;

import dev.maxuz.ttcli.model.Task;
import dev.maxuz.ttcli.printer.Printer;
import dev.maxuz.ttcli.service.TaskService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class CleanCommandTest {

    private final TaskService taskService = mock(TaskService.class);
    private final Printer printer = mock(Printer.class);

    @Test
    void listTasks() {
        List<Task> tasks = Arrays.asList(new Task(), new Task());
        when(taskService.getTasks()).thenReturn(tasks);

        CleanCommand command = new CleanCommand(taskService, printer);
        command.run();

        ArgumentCaptor<String> messagesArgumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(printer, times(2)).info(messagesArgumentCaptor.capture());
        verify(printer).info(tasks);
        verify(taskService).clean();

        List<String> printedMessages = messagesArgumentCaptor.getAllValues();
        assertThat(printedMessages.size()).isEqualTo(2);
        assertThat(printedMessages.get(0)).isEqualTo("Removing all tasks. Current task list is:");
        assertThat(printedMessages.get(1)).isEqualTo("Clean completed");
    }

}