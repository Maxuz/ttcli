package dev.maxuz.ttcli.command;

import dev.maxuz.ttcli.exception.TtRuntimeException;
import dev.maxuz.ttcli.model.TaskDay;
import dev.maxuz.ttcli.printer.Printer;
import dev.maxuz.ttcli.service.TaskDayService;
import dev.maxuz.ttcli.service.TaskService;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class StopCommandTest {
    private final TaskDayService taskDayService = mock(TaskDayService.class);
    private final TaskService taskService = mock(TaskService.class);
    private final Printer printer = mock(Printer.class);

    private TaskDay getTaskDay() {
        return new TaskDay(LocalDate.now());
    }

    @Test
    void stopTask_TaskExist_StopCalled() {
        TaskDay taskDay = getTaskDay();
        when(taskDayService.getCurrentDay()).thenReturn(taskDay);

        StopCommand stopCommand = new StopCommand(taskDayService, taskService, printer);
        stopCommand.run();

        verify(taskService).stop(taskDay);
        verify(taskDayService).save(taskDay);
        verify(printer).info("All tasks were stopped");
    }

    @Test
    void stopTask_CurrentDayIsNull_ThrowsException() {
        when(taskDayService.getCurrentDay()).thenReturn(null);

        StopCommand stopCommand = new StopCommand(taskDayService, taskService, printer);
        assertThatThrownBy(stopCommand::run)
            .isInstanceOf(TtRuntimeException.class)
            .hasMessage("Day is not started");

        verify(taskService, never()).stop(any());
        verify(taskDayService, never()).save(any());
    }
}