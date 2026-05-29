package com.quantacus.dashboard.service;

import com.quantacus.dashboard.exception.BusinessException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j
public class FileStorageService {

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    // Creates the uploads directory on startup if it does not already exist
    @PostConstruct
    public void init() {
        Path dir = Paths.get(uploadDir);
        if (!Files.exists(dir)) {
            try {
                Files.createDirectories(dir);
                log.info("Upload directory created at {}", dir.toAbsolutePath());
            } catch (IOException e) {
                throw new RuntimeException("Could not create upload directory: " + dir.toAbsolutePath(), e);
            }
        }
    }

    // Saves the file to disk with a UUID prefix to prevent filename collisions.
    // Returns the relative path string that is stored in the Job entity.
    public String save(MultipartFile file) {
        String originalName = StringUtils.cleanPath(
                Objects.requireNonNull(file.getOriginalFilename(), "Filename must not be null"));

        if (originalName.contains("..")) {
            throw new BusinessException("Filename contains invalid path sequence: " + originalName);
        }

        String storedName = UUID.randomUUID() + "_" + originalName;
        Path destination = Paths.get(uploadDir).resolve(storedName);

        try {
            Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
            log.info("File saved: {}", destination.toAbsolutePath());
            return destination.toString();
        } catch (IOException e) {
            throw new BusinessException("Failed to store file: " + e.getMessage());
        }
    }
}
