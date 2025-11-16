package com.exam.examapp.service.impl.information;

import com.exam.examapp.dto.request.information.SuperiorityRequest;
import com.exam.examapp.dto.request.information.SuperiorityUpdateRequest;
import com.exam.examapp.exception.custom.ResourceNotFoundException;
import com.exam.examapp.mapper.information.SuperiorityMapper;
import com.exam.examapp.model.enums.SuperiorityType;
import com.exam.examapp.model.information.Superiority;
import com.exam.examapp.repository.information.SuperiorityRepository;
import com.exam.examapp.service.impl.LocalFileServiceImpl;
import com.exam.examapp.service.interfaces.LogService;
import com.exam.examapp.service.interfaces.UserService;
import com.exam.examapp.service.interfaces.information.SuperiorityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SuperiorityServiceImpl implements SuperiorityService {
    private final static String IMAGE_PATH = "uploads/images/superiority";

    private final SuperiorityRepository superiorityRepository;

    private final UserService userService;

    private final LocalFileServiceImpl fileService;

    private final LogService logService;

    @Override
    public void createSuperiority(SuperiorityRequest request, MultipartFile icon) {
        log.info("Üstünlük yaradılır");
        Superiority superiority = SuperiorityMapper.requestTo(request);
        String iconUrl = fileService.uploadFile(IMAGE_PATH, icon);
        superiority.setIconUrl(iconUrl);
        superiorityRepository.save(superiority);
        log.info("Üstünlük yaradıldı");
        logService.save("Üstünlük yaradıldı", userService.getCurrentUserOrNull());
    }

    @Override
    public List<Superiority> getAllSuperiority() {
        return superiorityRepository.findAll();
    }

    @Override
    public List<Superiority> getSuperiorityBySuperiorityType(SuperiorityType type) {
        return superiorityRepository.getSuperiorityByType(type);
    }

    @Override
    public Superiority getSuperiorityById(UUID id) {
        return superiorityRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Üstünlük tapılmadı"));
    }

    @Override
    public void updateSuperiority(SuperiorityUpdateRequest request, MultipartFile icon) {
        log.info("Üstünlük yenilənir");
        Superiority superiority = getSuperiorityById(request.id());
        Superiority updatedSuperiority = SuperiorityMapper.updateRequestTo(superiority, request);
        if (icon != null) {
            fileService.deleteFile(IMAGE_PATH, superiority.getIconUrl());
            String uploadedIconUrl = fileService.uploadFile(IMAGE_PATH, icon);
            updatedSuperiority.setIconUrl(uploadedIconUrl);
        }
        superiorityRepository.save(updatedSuperiority);
        log.info("Üstünlük yeniləndi");
        logService.save("Üstünlük yeniləndi", userService.getCurrentUserOrNull());
    }

    @Override
    public void deleteSuperiority(UUID id) {
        log.info("Üstünlük silinir");
        Superiority superiority = getSuperiorityById(id);
        fileService.deleteFile(IMAGE_PATH, superiority.getIconUrl());
        superiorityRepository.deleteById(id);
        log.info("Üstünlük silindi");
        logService.save("Üstünlük silindi", userService.getCurrentUserOrNull());
    }
}
