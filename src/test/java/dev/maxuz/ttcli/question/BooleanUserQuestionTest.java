package dev.maxuz.ttcli.question;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.Console;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class BooleanUserQuestionTest {
    private final ConsoleWrapper console = mock(ConsoleWrapper.class);

    private static Stream<Arguments> askUser_CheckQuestion_Source() {
        return Stream.of(
            Arguments.of("Do you like ice-cream", true, "Do you like ice-cream [Y/n]?"),
            Arguments.of("Do you like ice-cream", false, "Do you like ice-cream [N/y]?")
            );
    }

    @ParameterizedTest
    @MethodSource("askUser_CheckQuestion_Source")
    void askUser_CheckQuestion(String question, boolean defaultValue, String expectedQuestion) {
        BooleanUserQuestion userQuestion = new BooleanUserQuestion(console, question, defaultValue);
        userQuestion.askUser();
        verify(console).readLine(expectedQuestion);
    }

    private static Stream<Arguments> askUser_CheckAnswers_Source() {
        return Stream.of(
            Arguments.of(true, null, true),
            Arguments.of(false, null, false),

            Arguments.of(true, "y", true),
            Arguments.of(true, "Y", true),
            Arguments.of(true, "yes", true),
            Arguments.of(true, "Yes", true),
            Arguments.of(true, "true", true),
            Arguments.of(true, "True", true),
            Arguments.of(true, "TRUE", true),

            Arguments.of(false, "y", true),
            Arguments.of(false, "y", true),
            Arguments.of(false, "yes", true),
            Arguments.of(false, "yes", true),
            Arguments.of(false, "true", true),
            Arguments.of(false, "true", true),
            Arguments.of(false, "TRUE", true),

            Arguments.of(true, "n", false),
            Arguments.of(true, "N", false),
            Arguments.of(true, "no", false),
            Arguments.of(true, "No", false),
            Arguments.of(true, "false", false),
            Arguments.of(true, "False", false),
            Arguments.of(true, "FALSE", false),

            Arguments.of(false, "n", false),
            Arguments.of(false, "N", false),
            Arguments.of(false, "no", false),
            Arguments.of(false, "No", false),
            Arguments.of(false, "false", false),
            Arguments.of(false, "False", false),
            Arguments.of(false, "FALSE", false)
            );
    }

    @ParameterizedTest
    @MethodSource("askUser_CheckAnswers_Source")
    void askUser_CheckAnswers(boolean defaultValue, String stringAnswer, boolean expectedAnswer) {
        when(console.readLine(anyString())).thenReturn(stringAnswer);
        BooleanUserQuestion userQuestion = new BooleanUserQuestion(console, "Do you like ice-cream", defaultValue);
        assertThat(userQuestion.askUser()).isEqualTo(expectedAnswer);
    }
}