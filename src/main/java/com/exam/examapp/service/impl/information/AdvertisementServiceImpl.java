package com.exam.examapp.service.impl.information;

import com.exam.examapp.dto.request.information.AdvertisementRequest;
import com.exam.examapp.dto.request.information.AdvertisementUpdateRequest;
import com.exam.examapp.exception.custom.BadRequestException;
import com.exam.examapp.exception.custom.ResourceNotFoundException;
import com.exam.examapp.mapper.information.AdvertisementMapper;
import com.exam.examapp.model.information.Advertisement;
import com.exam.examapp.repository.information.AdvertisementRepository;
import com.exam.examapp.service.impl.LocalFileServiceImpl;
import com.exam.examapp.service.interfaces.LogService;
import com.exam.examapp.service.interfaces.UserService;
import com.exam.examapp.service.interfaces.information.AdvertisementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdvertisementServiceImpl implements AdvertisementService {
    private final static String IMAGE_PATH = "uploads/images/advertisements";

    private final AdvertisementRepository advertisementRepository;

    private final LocalFileServiceImpl fileService;

    private final UserService userService;

    private final LogService logService;

    @Override
    public void saveAdvertisement(AdvertisementRequest request, MultipartFile image) {
        if (advertisementRepository.getAdvertisementByTitle(request.title()).isPresent())
            throw new BadRequestException("Başlığı " + request.title() + " olan reklam artıq mövcuddur");

        Advertisement advertisement = AdvertisementMapper
                .requestTo(request);

        String imageUrl = fileService.uploadFile(IMAGE_PATH, image);
        advertisement.setImageUrl(imageUrl);
        advertisementRepository.save(advertisement);
        log.info("Reklam yaradıldı. Reklam adı: {}", request.title());
        logService.save("Reklam yaradıldı. Reklam adı: " + request.title(), userService.getCurrentUserOrNull());
    }

    @Override
    public List<Advertisement> getAdvertisements() {
        return advertisementRepository.findAll();
    }

    @Override
    public Advertisement getAdvertisementById(UUID id) {
        return advertisementRepository.findById(id).orElseThrow(()->
                new ResourceNotFoundException("Reklam tapılmadı"));
    }

    @Override
    public Advertisement getAdvertisementByTitle(String title) {
        return advertisementRepository.getAdvertisementByTitle(title).orElseThrow(()->
                new ResourceNotFoundException("Reklam tapılmadı"));
    }

    @Override
    public void updateAdvertisement(AdvertisementUpdateRequest request, MultipartFile image) {
        log.info("Reklam yenilənir");
        Optional<Advertisement> advertisementByTitle = advertisementRepository.getAdvertisementByTitle(request.title());
        Advertisement advertisement = getAdvertisementById(request.id());

        if (advertisementByTitle.isPresent() &&
                request.id().equals(advertisementByTitle.get().getId()))
            throw new BadRequestException("Başlığı " + request.title() + " olan reklam artıq mövcuddur");

        Advertisement updatedAdvertisement = AdvertisementMapper.
                updateRequestTo(advertisement, request);
        fileService.deleteFile(IMAGE_PATH, advertisement.getImageUrl());
        advertisement.setImageUrl(fileService.uploadFile(IMAGE_PATH, image));
        advertisementRepository.save(updatedAdvertisement);
        log.info("Reklam yeniləndi");
        logService.save("Reklam yeniləndi. Reklam adı: " + request.title(), userService.getCurrentUserOrNull());
    }

    @Override
    public void deleteAdvertisement(UUID id) {
        log.info("Reklam silinir");
        Advertisement advertisement = getAdvertisementById(id);
        fileService.deleteFile(IMAGE_PATH, advertisement.getImageUrl());
        advertisementRepository.deleteById(id);
        log.info("Reklam silindi");
        logService.save("Reklam silindi", userService.getCurrentUserOrNull());
    }
}
