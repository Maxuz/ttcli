package dev.maxuz.ttcli.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Configuration
public class AppConfiguration {
    @Bean
    public FileDataProviderConfig fileDataProviderConfig(@Value("${storage.path}") String path) throws IOException {
        Path storage = Paths.get(path);
        if (!Files.exists(storage)) {
            Files.createFile(storage);
        }
        return new FileDataProviderConfig(storage);
    }
}
