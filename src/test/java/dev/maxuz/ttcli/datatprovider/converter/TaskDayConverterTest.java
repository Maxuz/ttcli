package dev.maxuz.ttcli.datatprovider.converter;

import dev.maxuz.ttcli.datatprovider.dto.TaskDayTO;
import dev.maxuz.ttcli.datatprovider.dto.TaskTO;
import dev.maxuz.ttcli.model.Task;
import dev.maxuz.ttcli.model.TaskDay;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TaskDayConverterTest {
    private final TaskConverter taskConverter = mock(TaskConverter.class);
    private final TaskDayConverter converter = new TaskDayConverter(taskConverter);

    @Test
    void convertToDto() {
        TaskTO expectedTask = new TaskTO();
        when(taskConverter.convert(any(Task.class)))
            .thenReturn(expectedTask);

        TaskDay source = new TaskDay(LocalDate.now());
        source.addTask(new Task());

        TaskDayTO actual = converter.convert(source);

        assertThat(actual).isNotNull();
        assertThat(actual.getDate()).isEqualTo(LocalDate.now().format(DateTimeFormatter.ISO_DATE));
        assertThat(actual.getTasks()).isNotNull();
        assertThat(actual.getTasks().size()).isEqualTo(1);
        assertThat(actual.getTasks().get(0)).isEqualTo(expectedTask);
    }

    @Test
    void convertFromDto() {
        Task expectedTask = new Task();
        when(taskConverter.convert(any(TaskTO.class)))
            .thenReturn(expectedTask);

        TaskDayTO source = new TaskDayTO();
        source.setDate(LocalDate.now().format(DateTimeFormatter.ISO_DATE));
        source.setTasks(Collections.singletonList(new TaskTO()));

        TaskDay actual = converter.convert(source);

        assertThat(actual).isNotNull();
        assertThat(actual.getDate()).isEqualTo(LocalDate.now());
        assertThat(actual.getTasks()).isNotNull();
        assertThat(actual.getTasks().size()).isEqualTo(1);
        assertThat(actual.getTasks().get(0)).isEqualTo(expectedTask);
    }
}