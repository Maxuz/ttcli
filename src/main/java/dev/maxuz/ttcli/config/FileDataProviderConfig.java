package dev.maxuz.ttcli.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.nio.file.Path;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileDataProviderConfig {
    private Path storage;
}
