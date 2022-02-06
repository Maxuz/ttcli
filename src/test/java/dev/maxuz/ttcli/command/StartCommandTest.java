package dev.maxuz.ttcli.command;

import dev.maxuz.ttcli.exception.TtRuntimeException;
import dev.maxuz.ttcli.model.Task;
import dev.maxuz.ttcli.model.TaskDay;
import dev.maxuz.ttcli.model.TaskState;
import dev.maxuz.ttcli.printer.Printer;
import dev.maxuz.ttcli.question.InteractiveQuestionnaire;
import dev.maxuz.ttcli.service.TaskDayService;
import dev.maxuz.ttcli.service.TaskService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

class StartCommandTest {
    private final TaskDayService taskDayService = mock(TaskDayService.class);
    private final TaskService taskService = mock(TaskService.class);
    private final Printer printer = mock(Printer.class);
    private final InteractiveQuestionnaire questionnaire = mock(InteractiveQuestionnaire.class);

    private TaskDay getTaskDay() {
        return new TaskDay(LocalDate.now());
    }

    @Test
    void startTask_TaskExists_StartTaskCalled() {
        Task task = new Task();
        task.setState(TaskState.WAITING);
        task.setName("TASK_CODE");

        TaskDay taskDay = getTaskDay();
        when(taskDayService.getCurrentDay()).thenReturn(taskDay);

        when(taskService.getTask(taskDay, "TASK_CODE"))
            .thenReturn(task);

        StartCommand command = new StartCommand(taskDayService, taskService, printer, questionnaire);
        command.setName(task.getName());

        command.run();

        verify(taskService).start(task);
        verify(taskDayService).save(taskDay);
        verify(printer).info("Task {} successfully started", "TASK_CODE");
    }

    @Test
    void startTask_TaskDoesNotExistUserWantsToCreate_TaskCreated() {
        TaskDay taskDay = getTaskDay();
        when(taskDayService.getCurrentDay()).thenReturn(taskDay);

        when(taskService.getTask(taskDay, "TASK_CODE"))
            .thenReturn(null);

        when(questionnaire.createTask("TASK_CODE")).thenReturn(true);

        StartCommand command = new StartCommand(taskDayService, taskService, printer, questionnaire);
        command.setName("TASK_CODE");

        command.run();

        verify(taskService, times(1)).addTask(any(), any());
        verify(taskService, times(1)).start(any());
        verify(taskDayService, times(2)).save(taskDay);
    }

    @Test
    void startTask_TaskDoesNotExistUserDoesNotWantToCreate_ThrowsException() {
        TaskDay taskDay = getTaskDay();
        when(taskDayService.getCurrentDay()).thenReturn(taskDay);

        when(taskService.getTask(taskDay, "TASK_CODE"))
            .thenReturn(null);

        when(questionnaire.createTask("TASK_CODE")).thenReturn(false);

        StartCommand command = new StartCommand(taskDayService, taskService, printer, questionnaire);
        command.setName("TASK_CODE");

        assertThatThrownBy(command::run)
            .isInstanceOf(TtRuntimeException.class)
            .hasMessage("Task with name [TASK_CODE] is not found");

        verify(taskService, times(0)).start(any());
        verify(taskDayService, never()).save(taskDay);
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
        TaskDay taskDay = getTaskDay();
        when(taskDayService.getCurrentDay()).thenReturn(taskDay);

        when(taskService.getTask(taskDay, "TASK_CODE"))
            .thenReturn(new Task());

        StartCommand command = new StartCommand(taskDayService, taskService, printer, questionnaire);
        command.setName("TASK_CODE");
        command.setStopOthers(stopOthers);

        command.run();

        if (stopOthers) {
            verify(taskService).stop(taskDay);
        } else {
            verify(taskService, times(0)).stop(any());
        }
        verify(taskDayService).save(taskDay);
    }

    @Test
    void startTask_DayIsNotStarted_StartTaskIsNotCalled() {
        Task task = new Task();
        task.setState(TaskState.WAITING);
        task.setName("TASK_CODE");

        when(taskDayService.getCurrentDay()).thenReturn(null);

        StartCommand command = new StartCommand(taskDayService, taskService, printer, questionnaire);
        command.setName(task.getName());

        Assertions.assertThatThrownBy(command::run)
            .isInstanceOf(TtRuntimeException.class)
            .hasMessage("Day is not started, so there is no tasks. Exiting.");

        verify(taskService, never()).start(any());
        verify(taskDayService, never()).save(any());
    }

}