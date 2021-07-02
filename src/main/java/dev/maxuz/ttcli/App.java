package dev.maxuz.ttcli;


import dev.maxuz.ttcli.command.AddCommand;
import dev.maxuz.ttcli.command.TtCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import picocli.CommandLine;

@Slf4j
@SpringBootApplication(scanBasePackages = "dev.maxuz.ttcli")
public class App implements CommandLineRunner {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    public App(AddCommand addCommand) {
        this.addCommand = addCommand;
    }

    private final AddCommand addCommand;

    @Override
    public void run(String... args) {
        int exitCode = new CommandLine(new TtCommand())
            .addSubcommand(addCommand)
            .execute(args);
        System.exit(exitCode);
    }
}
