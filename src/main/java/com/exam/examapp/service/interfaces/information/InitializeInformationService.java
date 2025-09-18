package com.exam.examapp.service.interfaces.information;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface InitializeInformationService {
    void initializeAdvertisement(List<MultipartFile> images);

    void initializeSuperiority(List<MultipartFile> icons);

    void initializeMediaContent(List<MultipartFile> images);
}
