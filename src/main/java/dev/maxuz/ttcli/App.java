package dev.maxuz.ttcli;


import dev.maxuz.ttcli.command.MainCommand;
import dev.maxuz.ttcli.command.SubCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import picocli.CommandLine;

import java.util.List;
import java.util.stream.Stream;

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
        CommandLine.IExecutionExceptionHandler errorHandler = (ex, cl, parseResult) -> {
            log.error("Error: {}\n", ex.getMessage());
            log.debug(ex.getMessage(), ex);
            cl.usage(cl.getErr());
            return cl.getCommandSpec().exitCodeOnExecutionException();
        };
        commandLine.setExecutionExceptionHandler(errorHandler);
        int exitCode = commandLine.execute(filterOut(args));
        System.exit(exitCode);
    }

    private String[] filterOut(String[] args) {
        return Stream.of(args).filter(v -> !v.startsWith("--spring")).toArray(String[]::new);
    }
}
