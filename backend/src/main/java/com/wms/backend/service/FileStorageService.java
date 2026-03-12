package com.wms.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path baseDir;

    public FileStorageService(@Value("${app.uploads.dir:uploads}") String uploadsDir) {
        this.baseDir = Path.of(uploadsDir).toAbsolutePath().normalize();
    }

    public StoredFile storeIssuePhoto(Long issueId, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("No file provided");
        }
        String original = StringUtils.cleanPath(file.getOriginalFilename() == null ? "photo" : file.getOriginalFilename());
        String ext = "";
        int dot = original.lastIndexOf('.');
        if (dot > 0 && dot < original.length() - 1) {
            ext = original.substring(dot);
        }

        String filename = UUID.randomUUID() + ext;
        Path dir = baseDir.resolve("issues").resolve(String.valueOf(issueId));
        Path target = dir.resolve(filename);

        try {
            Files.createDirectories(dir);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to store file", e);
        }

        String publicPath = "/uploads/issues/" + issueId + "/" + filename;
        return new StoredFile(publicPath, target);
    }

    public record StoredFile(String publicUrl, Path path) {
    }
}

