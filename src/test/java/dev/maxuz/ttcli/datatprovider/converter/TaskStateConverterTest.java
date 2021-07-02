package dev.maxuz.ttcli.datatprovider.converter;

import dev.maxuz.ttcli.datatprovider.dto.TaskStateTO;
import dev.maxuz.ttcli.model.TaskState;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class TaskStateConverterTest {
    private final TaskStateConverter converter = new TaskStateConverter();

    private static Stream<Arguments> convertToDtoSource() {
        return Stream.of(
            Arguments.of(null, null),
            Arguments.of(TaskState.WAITING, TaskStateTO.WAITING),
            Arguments.of(TaskState.IN_PROGRESS, TaskStateTO.IN_PROGRESS)
        );
    }

    @ParameterizedTest
    @MethodSource("convertToDtoSource")
    void convertToDto(TaskState source, TaskStateTO expected) {
        assertThat(converter.convert(source)).isEqualTo(expected);
    }

    @ParameterizedTest
    @EnumSource(value = TaskState.class)
    void convertToDtoAllHandled(TaskState source) {
        converter.convert(source);
    }

    private static Stream<Arguments> convertFromDtoSource() {
        return Stream.of(
            Arguments.of(null, null),
            Arguments.of(TaskStateTO.WAITING, TaskState.WAITING),
            Arguments.of(TaskStateTO.IN_PROGRESS, TaskState.IN_PROGRESS)
        );
    }

    @ParameterizedTest
    @MethodSource("convertFromDtoSource")
    void convertToDto(TaskStateTO source, TaskState expected) {
        assertThat(converter.convert(source)).isEqualTo(expected);
    }

    @ParameterizedTest
    @EnumSource(value = TaskStateTO.class)
    void convertToDtoAllHandled(TaskStateTO source) {
        converter.convert(source);
    }

}