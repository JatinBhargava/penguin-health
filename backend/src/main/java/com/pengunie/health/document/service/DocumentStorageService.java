package com.pengunie.health.document.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class DocumentStorageService {

    private final Path storageDirectory;

    public DocumentStorageService(
            @Value("${app.document-storage-path:./documents}") String storagePath) {

        this.storageDirectory = Paths.get(storagePath)
                .toAbsolutePath()
                .normalize();
    }

    public String store(MultipartFile file) throws IOException {

        Files.createDirectories(storageDirectory);

        String storedFileName = UUID.randomUUID() + ".pdf";

        Path target = storageDirectory
                .resolve(storedFileName)
                .normalize();

        if (!target.startsWith(storageDirectory)) {
            throw new IOException("Invalid file path");
        }

        Files.copy(file.getInputStream(), target);

        return target.toString();
    }
}