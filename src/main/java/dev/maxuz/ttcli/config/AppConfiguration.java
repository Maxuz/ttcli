package dev.maxuz.ttcli.config;

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
    public FileDataProviderConfig fileDataProviderConfig() throws IOException {
        Path storage = createStorage();
        return new FileDataProviderConfig(storage);
    }

    private Path createStorage() throws IOException {
        String storagePath = "./storage/";
        Path storageDir = Paths.get(storagePath);
        Files.createDirectories(storageDir);
        Path storage = Paths.get(storagePath + LocalDate.now().format(DateTimeFormatter.ISO_DATE) + ".json");
        if (!Files.exists(storage)) {
            Files.createFile(storage);
        }
        return storage;
    }
}
