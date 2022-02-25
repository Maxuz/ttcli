package dev.maxuz.ttcli.service;

import dev.maxuz.ttcli.datatprovider.TaskDayDataProvider;
import dev.maxuz.ttcli.exception.TtRuntimeException;
import dev.maxuz.ttcli.model.TaskDay;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

class TaskDayServiceTest {
    private final TaskDayDataProvider dayDataProvider = mock(TaskDayDataProvider.class);
    private final TaskDayService service = new TaskDayService(dayDataProvider);

    private static TaskDay createTaskDay(LocalDate date) {
        return new TaskDay(date);
    }

    private static TaskDay createTaskDay(String date) {
        return new TaskDay(date(date));
    }

    private static LocalDate date(String stringDate) {
        return LocalDate.parse(stringDate);
    }

    @Test
    void getCurrentDay_CurrentDayExists_ReturnCurrentDay() {
        TaskDay taskDay = createTaskDay(LocalDate.now());
        when(dayDataProvider.findAll()).thenReturn(singletonList(taskDay));

        TaskDay actual = service.getCurrentDay();
        assertThat(actual).isEqualTo(taskDay);
    }

    @Test
    void getCurrentDay_CurrentDayDoesNotExist_ReturnNull() {
        when(dayDataProvider.findAll()).thenReturn(emptyList());

        TaskDay actual = service.getCurrentDay();
        assertThat(actual).isNull();
    }

    @Test
    void getCurrentDay_CurrentDayHasFutureDate_ThrowsException() {
        TaskDay taskDay = createTaskDay(LocalDate.now().plusDays(1));
        when(dayDataProvider.findAll()).thenReturn(singletonList(taskDay));

        assertThatThrownBy(service::getCurrentDay)
            .isInstanceOf(TtRuntimeException.class)
            .hasMessage("Illegal state - current date can't be in the future");
    }

    @Test
    void getCurrentDay_CurrentDayIsInThePast_ReturnNull() {
        TaskDay taskDay = createTaskDay(LocalDate.now().minusDays(1));
        when(dayDataProvider.findAll()).thenReturn(singletonList(taskDay));

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

    private static Stream<Arguments> findMethodSource() {
        return Stream.of(
            Arguments.of(
                date("2022-02-01"),
                date("2022-02-02"),
                asList(createTaskDay("2022-02-01"), createTaskDay("2022-02-02")),
                asList(createTaskDay("2022-02-01"), createTaskDay("2022-02-02"))),
            Arguments.of(
                date("2022-02-02"),
                date("2022-02-02"),
                asList(createTaskDay("2022-02-01"), createTaskDay("2022-02-02"), createTaskDay("2022-02-03")),
                singletonList(createTaskDay("2022-02-02"))),
            Arguments.of(
                date("2022-02-02"),
                date("2022-02-02"),
                asList(createTaskDay("2022-02-01"), createTaskDay("2022-02-02")),
                singletonList(createTaskDay("2022-02-02"))),
            Arguments.of(
                date("2022-02-01"),
                date("2022-02-03"),
                asList(createTaskDay("2022-02-03"), createTaskDay("2022-02-04")),
                singletonList(createTaskDay("2022-02-03"))),
            Arguments.of(
                date("2022-02-10"),
                date("2022-02-10"),
                asList(createTaskDay("2022-02-01"), createTaskDay("2022-02-02")),
                emptyList())
        );
    }

    @ParameterizedTest
    @MethodSource("findMethodSource")
    void find(LocalDate from, LocalDate to, List<TaskDay> allDays, List<TaskDay> expected) {
        when(dayDataProvider.findAll()).thenReturn(allDays);

        assertThat(service.find(from, to)).isEqualTo(expected);
    }

    @Test
    void find_FromIsAfterToDate_ThrowsException() {
        assertThatThrownBy(() -> service.find(date("2022-02-10"), date("2022-02-09")))
            .isInstanceOf(TtRuntimeException.class)
            .hasMessage("To date can't be before from date");
    }
}