package com.balancerx.infrastructure.storage;

import com.balancerx.application.service.FileStoragePort;
import com.balancerx.domain.service.ChecksumService;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LocalFileStorageAdapter implements FileStoragePort {
    private static final Logger log = LoggerFactory.getLogger(LocalFileStorageAdapter.class);
    private final ChecksumService checksumService;

    @Value("${balancerx.storage.root:files}")
    private String rootDir;

    @Override
    public StoredFile storePdf(String fileName, byte[] content) {
        try {
            Path basePath = Path.of(rootDir, LocalDate.now().toString());
            Files.createDirectories(basePath);
            String cleanName = UUID.randomUUID() + "-" + fileName;
            Path destination = basePath.resolve(cleanName);
            Files.write(destination, content, StandardOpenOption.CREATE_NEW);
            String checksum = checksumService.sha256(content);
            log.info("Archivo guardado en {} con checksum {}", destination, checksum);
            return new StoredFile(destination.toString(), checksum);
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo almacenar el archivo", e);
        }
    }
}
