package dev.maxuz.ttcli.datatprovider.converter;

import dev.maxuz.ttcli.datatprovider.dto.TaskStateTO;
import dev.maxuz.ttcli.datatprovider.dto.TaskTO;
import dev.maxuz.ttcli.model.Task;
import dev.maxuz.ttcli.model.TaskState;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TaskConverterTest {

    private final TaskStateConverter taskStateConverter = mock(TaskStateConverter.class);
    private final TaskConverter converter = new TaskConverter(taskStateConverter);

    @Test
    void convertToDto() {
        when(taskStateConverter.convert(TaskState.IN_PROGRESS))
            .thenReturn(TaskStateTO.IN_PROGRESS);
        Task source = new Task();
        source.setName("task code");
        source.setState(TaskState.IN_PROGRESS);
        source.setTimeSpent(100);
        source.setStartTime(10000L);

        TaskTO actual = converter.convert(source);

        assertThat(actual).isNotNull();
        assertThat(actual.getName()).isEqualTo("task code");
        assertThat(actual.getState()).isEqualTo(TaskStateTO.IN_PROGRESS);
        assertThat(actual.getTimeSpent()).isEqualTo(100);
        assertThat(actual.getStartTime()).isEqualTo(10000L);
    }

    @Test
    void convertFromDto() {
        when(taskStateConverter.convert(TaskStateTO.IN_PROGRESS))
            .thenReturn(TaskState.IN_PROGRESS);
        TaskTO source = new TaskTO();
        source.setName("task code");
        source.setState(TaskStateTO.IN_PROGRESS);
        source.setTimeSpent(100);
        source.setStartTime(10000L);

        Task actual = converter.convert(source);

        assertThat(actual).isNotNull();
        assertThat(actual.getName()).isEqualTo("task code");
        assertThat(actual.getState()).isEqualTo(TaskState.IN_PROGRESS);
        assertThat(actual.getTimeSpent()).isEqualTo(100);
        assertThat(actual.getStartTime()).isEqualTo(10000L);
    }
}