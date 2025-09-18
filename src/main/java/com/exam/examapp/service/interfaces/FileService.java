package com.exam.examapp.service.interfaces;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    String uploadFile(String directory, MultipartFile file);

    void deleteFile(String directory, String fileName);
}
