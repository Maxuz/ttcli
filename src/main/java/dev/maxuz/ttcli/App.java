package dev.maxuz.ttcli;


import dev.maxuz.ttcli.command.MainCommand;
import dev.maxuz.ttcli.command.SubCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import picocli.CommandLine;

import java.util.List;

@Slf4j
@SpringBootApplication(scanBasePackages = "dev.maxuz.ttcli")
public class App implements CommandLineRunner {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    public App(List<SubCommand> subCommands) {
        this.subCommands = subCommands;
    }

    private final List<SubCommand> subCommands;

    @Override
    public void run(String... args) {
        CommandLine commandLine = new CommandLine(new MainCommand());
        subCommands.forEach(commandLine::addSubcommand);
        int exitCode = commandLine.execute(args);
        System.exit(exitCode);
    }
}
