package dev.maxuz.ttcli.printer;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class TimeConverterTest {
    private final TimeConverter converter = new TimeConverter();

    private static Stream<Arguments> convertTimeSource() {
        return Stream.of(
            Arguments.of(1L, "0h 0m 0s"),
            Arguments.of(1000L, "0h 0m 1s"),
            Arguments.of(7000L, "0h 0m 7s"),
            Arguments.of(59000L, "0h 0m 59s"),
            Arguments.of(60000L, "0h 1m 0s"),
            Arguments.of(68000L, "0h 1m 8s"),
            Arguments.of(3599000L, "0h 59m 59s"),
            Arguments.of(3600000L, "1h 0m 0s"),
            Arguments.of(3601000L, "1h 0m 1s"),
            Arguments.of(7199000L, "1h 59m 59s"),
            Arguments.of(12424000L, "3h 27m 4s")
        );
    }

    @ParameterizedTest
    @MethodSource("convertTimeSource")
    void convert(long time, String expected) {
        assertEquals(expected, converter.convert(time));
    }
}