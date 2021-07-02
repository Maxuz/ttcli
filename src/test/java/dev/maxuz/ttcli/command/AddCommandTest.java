package dev.maxuz.ttcli.command;

import dev.maxuz.ttcli.model.Task;
import dev.maxuz.ttcli.model.TaskState;
import dev.maxuz.ttcli.service.TaskService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;

import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

class AddCommandTest {
    private final TaskService taskService = mock(TaskService.class);

    @Test
    void addTask() {
        AddCommand command = new AddCommand(taskService);
        command.setCode("TASK_CODE");

        command.run();

        ArgumentCaptor<Task> taskArgumentCaptor = ArgumentCaptor.forClass(Task.class);
        verify(taskService).addTask(taskArgumentCaptor.capture());
        Task task = taskArgumentCaptor.getValue();
        assertThat(task.getCode()).isEqualTo("TASK_CODE");
        assertThat(task.getState()).isEqualTo(TaskState.WAITING);
    }

    private static Stream<Arguments> addTaskStartImmediatelySource() {
        return Stream.of(
            Arguments.of(true, TaskState.IN_PROGRESS),
            Arguments.of(false, TaskState.WAITING)
        );
    }

    @ParameterizedTest
    @MethodSource("addTaskStartImmediatelySource")
    void addTask_StartImmediatelyIsTrue(boolean startImmediately, TaskState expected) {
        AddCommand command = new AddCommand(taskService);
        command.setCode("TASK_CODE");
        command.setStartImmediately(startImmediately);

        command.run();

        if (startImmediately) {
            verify(taskService).stopCurrent();
        } else {
            verify(taskService, times(0)).stopCurrent();
        }

        ArgumentCaptor<Task> taskArgumentCaptor = ArgumentCaptor.forClass(Task.class);
        verify(taskService).addTask(taskArgumentCaptor.capture());
        Task task = taskArgumentCaptor.getValue();
        assertThat(task.getState()).isEqualTo(expected);
    }
}