package com.vsa.ecommerce.common.service.impl;

import com.vsa.ecommerce.common.service.StorageService;
import com.vsa.ecommerce.common.util.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@Slf4j
public class LocalStorageServiceImpl implements StorageService {

    private final Path rootLocation = Paths.get("uploads");

    public LocalStorageServiceImpl() {
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage", e);
        }
    }

    @Override
    public String upload(MultipartFile file, String folder) {
        if (file.isEmpty()) {
            throw new RuntimeException("Failed to store empty file.");
        }
        try {
            String filename = UUID.randomUUID().toString() + "." + FileUtil.getExtension(file.getOriginalFilename());
            return upload(file.getInputStream(), filename, folder);
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file " + file.getOriginalFilename(), e);
        }
    }

    @Override
    public String upload(InputStream inputStream, String fileName, String folder) {
        try {
            Path folderPath = rootLocation.resolve(folder);
            if (!Files.exists(folderPath)) {
                Files.createDirectories(folderPath);
            }
            Path destinationFile = folderPath.resolve(fileName);
            Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            return destinationFile.toString(); // Return local path for now
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }

    @Override
    public void delete(String fileUrl) {
        // Naive implementation assuming fileUrl is the local path
        try {
            Path file = Paths.get(fileUrl);
            Files.deleteIfExists(file);
        } catch (IOException e) {
            log.error("Could not delete file: {}", fileUrl, e);
        }
    }
}
