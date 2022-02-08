package dev.maxuz.ttcli.command;

import dev.maxuz.ttcli.exception.TtRuntimeException;
import dev.maxuz.ttcli.model.Task;
import dev.maxuz.ttcli.model.TaskDay;
import dev.maxuz.ttcli.printer.Printer;
import dev.maxuz.ttcli.service.TaskDayService;
import dev.maxuz.ttcli.service.TaskService;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class ListCommandTest {
    private final TaskDayService taskDayService = mock(TaskDayService.class);
    private final Printer printer = mock(Printer.class);

    @Test
    void listTasks() {
        TaskDay taskDay = new TaskDay(LocalDate.now());
        taskDay.addTask(new Task());
        taskDay.addTask(new Task());

        when(taskDayService.getCurrentDay()).thenReturn(taskDay);

        ListCommand command = new ListCommand(taskDayService, printer);
        command.run();

        verify(printer).info(taskDay.getTasks());
    }

    @Test
    void listTask_CurrentDayIsNull_NothingToShowMessage() {
        when(taskDayService.getCurrentDay()).thenReturn(null);

        ListCommand command = new ListCommand(taskDayService, printer);
        command.run();
        verify(printer).info("There is now started day - nothing to show");
    }
}