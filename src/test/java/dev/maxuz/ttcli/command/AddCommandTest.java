package dev.maxuz.ttcli.command;

import dev.maxuz.ttcli.exception.TtRuntimeException;
import dev.maxuz.ttcli.model.Task;
import dev.maxuz.ttcli.model.TaskDay;
import dev.maxuz.ttcli.model.TaskState;
import dev.maxuz.ttcli.printer.Printer;
import dev.maxuz.ttcli.question.InteractiveQuestionnaire;
import dev.maxuz.ttcli.service.TaskDayService;
import dev.maxuz.ttcli.service.TaskService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

class AddCommandTest {
    private final TaskDayService taskDayService = mock(TaskDayService.class);
    private final TaskService taskService = mock(TaskService.class);
    private final Printer printer = mock(Printer.class);
    private final InteractiveQuestionnaire questionnaire = mock(InteractiveQuestionnaire.class);

    private TaskDay getTaskDay() {
        return new TaskDay(LocalDate.now());
    }

    @Test
    void addTask() {
        AddCommand command = new AddCommand(taskDayService, taskService, printer, questionnaire);
        command.setName("TASK_CODE");

        TaskDay taskDay = getTaskDay();
        when(taskDayService.getCurrentDay()).thenReturn(taskDay);

        command.run();

        ArgumentCaptor<Task> taskArgumentCaptor = ArgumentCaptor.forClass(Task.class);
        verify(taskService).addTask(eq(taskDay), taskArgumentCaptor.capture());
        verify(taskDayService, times(2)).save(taskDay);
        Task task = taskArgumentCaptor.getValue();
        assertThat(task.getName()).isEqualTo("TASK_CODE");
        assertThat(task.getState()).isEqualTo(TaskState.WAITING);
    }

    @Test
    void addTask_StartImmediatelyIsTrue() {
        AddCommand command = new AddCommand(taskDayService, taskService, printer, questionnaire);
        command.setName("TASK_CODE");
        command.setStartImmediately(true);

        TaskDay taskDay = getTaskDay();
        when(taskDayService.getCurrentDay()).thenReturn(taskDay);

        command.run();

        ArgumentCaptor<Task> taskArgumentCaptor = ArgumentCaptor.forClass(Task.class);
        verify(taskService).addTask(eq(taskDay), taskArgumentCaptor.capture());
        Task task = taskArgumentCaptor.getValue();
        verify(taskService).stop(taskDay);
        verify(taskService).start(task);
        verify(taskDayService, times(2)).save(taskDay);
        verify(printer).info("Task {} added", "TASK_CODE");
    }

    @Test
    void addTask_StartNewDay_StartNewDayIsCalled() {
        AddCommand command = new AddCommand(taskDayService, taskService, printer, questionnaire);
        command.setName("TASK_CODE");
        command.setStartNewDay(true);

        TaskDay taskDay = getTaskDay();
        when(taskDayService.getCurrentDay()).thenReturn(taskDay);

        command.run();

        ArgumentCaptor<Task> taskArgumentCaptor = ArgumentCaptor.forClass(Task.class);
        verify(taskService).addTask(eq(taskDay), taskArgumentCaptor.capture());
        verify(taskDayService, times(3)).save(taskDay);
        verify(printer).info("Task {} added", "TASK_CODE");
    }

    @Test
    void addTask_CurrentDayIsNull_UserShouldBeAskedToStartNewDay() {
        AddCommand command = new AddCommand(taskDayService, taskService, printer, questionnaire);
        command.setName("TASK_CODE");

        when(taskDayService.getCurrentDay()).thenReturn(null);
        when(questionnaire.askStartNewDay()).thenReturn(true);

        command.run();

        verify(questionnaire).askStartNewDay();
    }

    @Test
    void addTask_CurrentDayIsNullUserDoesNotWantToStartDay_NewTaskIsNotAdded() {
        AddCommand command = new AddCommand(taskDayService, taskService, printer, questionnaire);
        command.setName("TASK_CODE");

        when(taskDayService.getCurrentDay()).thenReturn(null);
        when(questionnaire.askStartNewDay()).thenReturn(false);


        assertThatThrownBy(command::run)
            .isInstanceOf(TtRuntimeException.class)
                .hasMessage("New day is required. Exiting.");

        verify(questionnaire).askStartNewDay();
        verify(taskService, never()).addTask(any(), any());
    }
}