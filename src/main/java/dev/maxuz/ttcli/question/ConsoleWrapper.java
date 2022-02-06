package dev.maxuz.ttcli.question;

import java.io.Console;

/**
 * We need this class because Console is final, and we can't use Mockito to test classes that use Console.
 */
public class ConsoleWrapper {
    private final Console console = System.console();

   public String readLine(String fmt, Object... args) {
       return console.readLine(fmt, args);
   }
}
