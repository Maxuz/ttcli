package dev.maxuz.ttcli.command;

import dev.maxuz.ttcli.model.Task;
import dev.maxuz.ttcli.model.TaskDay;
import dev.maxuz.ttcli.printer.Printer;
import dev.maxuz.ttcli.printer.TimeConverter;
import dev.maxuz.ttcli.service.TaskDayService;
import dev.maxuz.ttcli.service.TaskService;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

import static org.mockito.Mockito.*;

class ListCommandTest {
    private final TaskDayService taskDayService = mock(TaskDayService.class);
    private final TaskService taskService = mock(TaskService.class);
    private final TimeConverter timeConverter = mock(TimeConverter.class);
    private final Printer printer = mock(Printer.class);

    @Test
    void listTasks() {
        TaskDay taskDay = new TaskDay(LocalDate.now());
        taskDay.addTask(new Task());
        taskDay.addTask(new Task());

        when(taskDayService.find(any(), any())).thenReturn(Collections.singletonList(taskDay));

        ListCommand command = new ListCommand(taskDayService, taskService, timeConverter, printer);
        command.run();

        verify(printer).info(taskDay);
    }

    @Test
    void listTask_CurrentDayIsNull_NothingToShowMessage() {
        when(taskDayService.find(any(), any())).thenReturn(Collections.emptyList());

        ListCommand command = new ListCommand(taskDayService, taskService, timeConverter, printer);
        command.run();
        verify(printer).info("No task days found");
    }

    @Test
    void listTasks_NoDateSet_FindIsCalledWithCurrentDates() {
        when(taskDayService.find(any(), any())).thenReturn(Collections.emptyList());

        ListCommand command = new ListCommand(taskDayService, taskService, timeConverter, printer);
        command.run();

        verify(taskDayService).find(LocalDate.now(), LocalDate.now());
    }

    @Test
    void listTasks_DatesAreSet_FindIsCalledWithCorrectDates() {
        when(taskDayService.find(any(), any())).thenReturn(Collections.emptyList());

        ListCommand command = new ListCommand(taskDayService, taskService, timeConverter, printer);
        command.setFromDate(LocalDate.now().minusDays(1).format(DateTimeFormatter.ISO_DATE));
        command.setToDate(LocalDate.now().plusDays(1).format(DateTimeFormatter.ISO_DATE));
        command.run();

        verify(taskDayService).find(LocalDate.now().minusDays(1), LocalDate.now().plusDays(1));
    }
}