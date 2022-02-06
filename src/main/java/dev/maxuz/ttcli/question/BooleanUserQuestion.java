package dev.maxuz.ttcli.question;

import java.io.Console;

public class BooleanUserQuestion {
    private final ConsoleWrapper console;
    private final String question;
    private final boolean defaultValue;

    public BooleanUserQuestion(ConsoleWrapper console, String question, boolean defaultValue) {
        this.console = console;
        this.question = addAnswers(question, defaultValue);
        this.defaultValue = defaultValue;
    }

    private static String addAnswers(String question, boolean defaultValue) {
        if (defaultValue) {
            return question + " [Y/n]?";
        } else {
            return question + " [N/y]?";
        }
    }

    public boolean askUser() {
        String answer = console.readLine(question);
        if (answer == null || answer.isEmpty()) {
            return defaultValue;
        }
        return Boolean.parseBoolean(answer) || "y".equalsIgnoreCase(answer) || "yes".equalsIgnoreCase(answer);
    }
}
