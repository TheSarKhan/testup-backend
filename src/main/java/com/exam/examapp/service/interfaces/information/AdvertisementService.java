package com.exam.examapp.service.interfaces.information;

import com.exam.examapp.dto.request.information.AdvertisementRequest;
import com.exam.examapp.dto.request.information.AdvertisementUpdateRequest;
import com.exam.examapp.model.information.Advertisement;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface AdvertisementService {
    void saveAdvertisement(AdvertisementRequest request, MultipartFile image);

    List<Advertisement> getAdvertisements();

    Advertisement getAdvertisementById(UUID id);

    Advertisement getAdvertisementByTitle(String title);

    void updateAdvertisement(AdvertisementUpdateRequest request, MultipartFile image);

    void deleteAdvertisement(UUID id);
}
