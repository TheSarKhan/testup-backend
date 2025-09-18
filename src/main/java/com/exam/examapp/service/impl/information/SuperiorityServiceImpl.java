package com.exam.examapp.service.impl.information;

import com.exam.examapp.dto.request.information.SuperiorityRequest;
import com.exam.examapp.dto.request.information.SuperiorityUpdateRequest;
import com.exam.examapp.exception.custom.ResourceNotFoundException;
import com.exam.examapp.mapper.information.SuperiorityMapper;
import com.exam.examapp.model.enums.SuperiorityType;
import com.exam.examapp.model.information.Superiority;
import com.exam.examapp.repository.information.SuperiorityRepository;
import com.exam.examapp.service.impl.LocalFileServiceImpl;
import com.exam.examapp.service.interfaces.information.SuperiorityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SuperiorityServiceImpl implements SuperiorityService {
    private final static String IMAGE_PATH = "uploads/images/superiority";

    private final SuperiorityRepository superiorityRepository;

    private final LocalFileServiceImpl fileService;

    @Override
    public void createSuperiority(SuperiorityRequest request, MultipartFile icon) {
        Superiority superiority = SuperiorityMapper.requestTo(request);
        String iconUrl = fileService.uploadFile(IMAGE_PATH, icon);
        superiority.setIconUrl(iconUrl);
        superiorityRepository.save(superiority);
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
        return superiorityRepository.findById(id).orElseThrow(()->
                new ResourceNotFoundException("Superiority not found."));
    }

    @Override
    public void updateSuperiority(SuperiorityUpdateRequest request, MultipartFile icon) {
        Superiority superiority = getSuperiorityById(request.id());
        Superiority updatedSuperiority = SuperiorityMapper.updateRequestTo(superiority, request);
        fileService.deleteFile(IMAGE_PATH, superiority.getIconUrl());
        String uploadedIconUrl = fileService.uploadFile(IMAGE_PATH, icon);
        updatedSuperiority.setIconUrl(uploadedIconUrl);
        superiorityRepository.save(updatedSuperiority);
    }

    @Override
    public void deleteSuperiority(UUID id) {
        Superiority superiority = getSuperiorityById(id);
        fileService.deleteFile(IMAGE_PATH, superiority.getIconUrl());
        superiorityRepository.deleteById(id);
    }
}
