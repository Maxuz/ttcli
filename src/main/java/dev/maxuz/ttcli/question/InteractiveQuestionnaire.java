package dev.maxuz.ttcli.question;

import org.springframework.stereotype.Service;

@Service
public class InteractiveQuestionnaire {
    private final ConsoleWrapper consoleWrapper = new ConsoleWrapper();
    private final BooleanUserQuestion startNewDay = new BooleanUserQuestion(consoleWrapper, "New day is not started. Would you like to start a new day", true);

    public boolean askStartNewDay() {
        return startNewDay.askUser();
    }

}
