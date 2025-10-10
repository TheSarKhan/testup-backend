package com.exam.examapp.service.impl;

import com.exam.examapp.exception.custom.DirectoryException;
import com.exam.examapp.exception.custom.FileException;
import com.exam.examapp.service.interfaces.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
public class LocalFileServiceImpl implements FileService {
    @Value("${app.base-url}")
    private String baseUrl;

    @Override
    public String uploadFile(String directory, MultipartFile file) {
        log.info("Şəkil endirilir");
        if (file.isEmpty() || Objects.isNull(file.getOriginalFilename()))
            return null;

        String originalFilename = file.getOriginalFilename();

        originalFilename = originalFilename.replaceAll("[^a-zA-Z0-9.]", "");

        Path uploadDir = Paths.get(directory);
        if (!Files.exists(uploadDir)) {
            try {
                Files.createDirectories(uploadDir);
            } catch (IOException e) {
                throw new DirectoryException("Qovluq yaradıla bilmədi");
            }
        }

        String fileName = UUID.randomUUID() + "_" + originalFilename;
        Path filePath = uploadDir.resolve(fileName);

        try {
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new FileException("Şəkil yaradıla bilmədi");
        }

        return baseUrl + "/" + filePath;
    }

    @Override
    public void deleteFile(String directory, String imageUrl) {
        log.info("Şəkil silinir");
        String fileName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);

        Path uploadDir = Paths.get(directory);
        Path filePath = uploadDir.resolve(fileName);

        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new FileException("Şəkil silinə bilmədi");
        }
    }
}
