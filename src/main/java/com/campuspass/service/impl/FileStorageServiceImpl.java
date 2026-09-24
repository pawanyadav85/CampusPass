package com.campuspass.service.impl;

import com.campuspass.exception.BadRequestException;
import com.campuspass.exception.ResourceNotFoundException;
import com.campuspass.service.FileStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    private final Path rootLocation;
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(".pdf", ".jpg", ".jpeg", ".png");
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    public FileStorageServiceImpl(@Value("${campuspass.upload.dir:./uploads}") String uploadDir) {
        this.rootLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.rootLocation);
        } catch (IOException ex) {
            throw new RuntimeException("Could not initialize upload storage location", ex);
        }
    }

    @Override
    public String storeFile(MultipartFile file, String subDirectory) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Failed to store empty file");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BadRequestException("File size exceeds 5MB limit");
        }

        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename() != null ? file.getOriginalFilename() : "document");
        String extension = "";
        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex >= 0) {
            extension = originalFilename.substring(dotIndex).toLowerCase();
        }

        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new BadRequestException("Invalid file type. Only PDF, JPG, and PNG are allowed");
        }

        try {
            Path targetDir = this.rootLocation.resolve(subDirectory).normalize();
            Files.createDirectories(targetDir);

            String uniqueFilename = UUID.randomUUID().toString() + extension;
            Path targetPath = targetDir.resolve(uniqueFilename);

            // Path traversal safety check
            if (!targetPath.getParent().equals(targetDir)) {
                throw new BadRequestException("Cannot store file outside current directory");
            }

            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            return subDirectory + "/" + uniqueFilename;
        } catch (IOException ex) {
            throw new RuntimeException("Failed to store file: " + originalFilename, ex);
        }
    }

    @Override
    public Resource loadFileAsResource(String relativePath) {
        try {
            Path filePath = this.rootLocation.resolve(relativePath).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new ResourceNotFoundException("File not found or unreadable: " + relativePath);
            }
        } catch (MalformedURLException ex) {
            throw new ResourceNotFoundException("File path invalid: " + relativePath);
        }
    }

    @Override
    public void deleteFile(String relativePath) {
        try {
            Path filePath = this.rootLocation.resolve(relativePath).normalize();
            Files.deleteIfExists(filePath);
        } catch (IOException ignored) {
        }
    }
}
