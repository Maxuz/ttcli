package dev.maxuz.ttcli.service;

import dev.maxuz.ttcli.datatprovider.TaskDayDataProvider;
import dev.maxuz.ttcli.exception.TtRuntimeException;
import dev.maxuz.ttcli.model.TaskDay;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Collections;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

class TaskDayServiceTest {
    private final TaskDayDataProvider dayDataProvider = mock(TaskDayDataProvider.class);
    private final TaskDayService service = new TaskDayService(dayDataProvider);

    private TaskDay createTaskDay(LocalDate date) {
        return new TaskDay(date);
    }

    @Test
    void getCurrentDay_CurrentDayExists_ReturnCurrentDay() {
        TaskDay taskDay = createTaskDay(LocalDate.now());
        when(dayDataProvider.findAll()).thenReturn(Collections.singletonList(taskDay));

        TaskDay actual = service.getCurrentDay();
        assertThat(actual).isEqualTo(taskDay);
    }

    @Test
    void getCurrentDay_CurrentDayDoesNotExist_ReturnNull() {
        when(dayDataProvider.findAll()).thenReturn(Collections.emptyList());

        TaskDay actual = service.getCurrentDay();
        assertThat(actual).isNull();
    }

    @Test
    void getCurrentDay_CurrentDayHasFutureDate_ThrowsException() {
        TaskDay taskDay = createTaskDay(LocalDate.now().plusDays(1));
        when(dayDataProvider.findAll()).thenReturn(Collections.singletonList(taskDay));

        assertThatThrownBy(service::getCurrentDay)
            .isInstanceOf(TtRuntimeException.class)
            .hasMessage("Illegal state - current date can't be in the future");
    }

    @Test
    void getCurrentDay_CurrentDayIsInThePast_ReturnNull() {
        TaskDay taskDay = createTaskDay(LocalDate.now().minusDays(1));
        when(dayDataProvider.findAll()).thenReturn(Collections.singletonList(taskDay));

        TaskDay actual = service.getCurrentDay();
        assertThat(actual).isNull();
    }

    @Test
    void addDay_DayIsNotNull_SaveIsCalled() {
        TaskDay taskDay = createTaskDay(LocalDate.now());

        service.save(taskDay);
        verify(dayDataProvider, times(1)).save(taskDay);
    }

    @Test
    void addDay_DayIsNull_ThrowsException() {
        assertThatThrownBy(() -> service.save(null))
            .isInstanceOf(TtRuntimeException.class)
            .hasMessage("Task day can't be null");
    }
}