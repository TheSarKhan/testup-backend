package com.exam.examapp.service.impl.information;

import com.exam.examapp.dto.request.information.AdvertisementRequest;
import com.exam.examapp.dto.request.information.AdvertisementUpdateRequest;
import com.exam.examapp.exception.custom.BadRequestException;
import com.exam.examapp.exception.custom.ResourceNotFoundException;
import com.exam.examapp.mapper.information.AdvertisementMapper;
import com.exam.examapp.model.information.Advertisement;
import com.exam.examapp.repository.information.AdvertisementRepository;
import com.exam.examapp.service.impl.LocalFileServiceImpl;
import com.exam.examapp.service.interfaces.information.AdvertisementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdvertisementServiceImpl implements AdvertisementService {
    private final static String IMAGE_PATH = "uploads/images/advertisements";

    private final AdvertisementRepository advertisementRepository;

    private final LocalFileServiceImpl fileService;

    @Override
    public void saveAdvertisement(AdvertisementRequest request, MultipartFile image) {
        if (advertisementRepository.getAdvertisementByTitle(request.title()).isPresent())
            throw new BadRequestException("Advertisement with title " + request.title() + " already exists.");

        Advertisement advertisement = AdvertisementMapper
                .requestTo(request);

        String imageUrl = fileService.uploadFile(IMAGE_PATH, image);
        advertisement.setImageUrl(imageUrl);
        advertisementRepository.save(advertisement);
    }

    @Override
    public List<Advertisement> getAdvertisements() {
        return advertisementRepository.findAll();
    }

    @Override
    public Advertisement getAdvertisementById(UUID id) {
        return advertisementRepository.findById(id).orElseThrow(()->
                new ResourceNotFoundException("Advertisement not found."));
    }

    @Override
    public Advertisement getAdvertisementByTitle(String title) {
        return advertisementRepository.getAdvertisementByTitle(title).orElseThrow(()->
                new ResourceNotFoundException("Advertisement not found."));
    }

    @Override
    public void updateAdvertisement(AdvertisementUpdateRequest request, MultipartFile image) {
        Optional<Advertisement> advertisementByTitle = advertisementRepository.getAdvertisementByTitle(request.title());
        Advertisement advertisement = getAdvertisementById(request.id());

        if (advertisementByTitle.isPresent() &&
                request.id().equals(advertisementByTitle.get().getId()))
            throw new BadRequestException("Advertisement with title " + request.title() + " already exists.");

        Advertisement updatedAdvertisement = AdvertisementMapper.
                updateRequestTo(advertisement, request);
        fileService.deleteFile(IMAGE_PATH, advertisement.getImageUrl());
        advertisement.setImageUrl(fileService.uploadFile(IMAGE_PATH, image));
        advertisementRepository.save(updatedAdvertisement);
    }

    @Override
    public void deleteAdvertisement(UUID id) {
        Advertisement advertisement = getAdvertisementById(id);
        fileService.deleteFile(IMAGE_PATH, advertisement.getImageUrl());
        advertisementRepository.deleteById(id);
    }
}
