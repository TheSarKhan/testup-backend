package com.exam.examapp.service.impl.subject;

import com.exam.examapp.dto.request.subject.SubmoduleRequest;
import com.exam.examapp.dto.request.subject.SubmoduleUpdateRequest;
import com.exam.examapp.exception.custom.BadRequestException;
import com.exam.examapp.exception.custom.ResourceNotFoundException;
import com.exam.examapp.model.exam.Module;
import com.exam.examapp.model.exam.Submodule;
import com.exam.examapp.repository.subject.SubmoduleRepository;
import com.exam.examapp.service.interfaces.FileService;
import com.exam.examapp.service.interfaces.subject.ModuleService;
import com.exam.examapp.service.interfaces.subject.SubmoduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SubmoduleServiceImpl implements SubmoduleService {
    private static final String IMAGE_PATH= "uploads/images/sub-modules";

    private final SubmoduleRepository submoduleRepository;

    private final ModuleService moduleService;

    private final FileService  fileService;

    @Override
    public void create(SubmoduleRequest request, MultipartFile logo) {
        if (submoduleRepository.existsByName(request.name()))
            throw new BadRequestException("Submodule already exists");

        Module module = moduleService.getModuleById(request.moduleId());
        Submodule build = Submodule.builder().name(request.name()).module(module).build();
        String logoUrl = fileService.uploadFile(IMAGE_PATH, logo);
        build.setLogoUrl(logoUrl);
        submoduleRepository.save(build);
    }

    @Override
    public List<Submodule> getAll() {
        return submoduleRepository.findAll();
    }

    @Override
    public List<Submodule> getAllByModule(UUID moduleId) {
        return submoduleRepository.getAllByModule_Id(moduleId);
    }

    @Override
    public Submodule getById(UUID id) {
        return submoduleRepository.findById(id).orElseThrow(()->
                new ResourceNotFoundException("Submodule not found"));
    }

    @Override
    public Submodule getByName(String name) {
        return submoduleRepository.getByName(name).orElseThrow(()->
                new ResourceNotFoundException("Submodule not found"));
    }

    @Override
    public void update(SubmoduleUpdateRequest request, MultipartFile logo) {
        Submodule submodule = getById(request.id());
        Optional<Submodule> byName = submoduleRepository.getByName(request.name());
        if (byName.isPresent() && !byName.get().getId().equals(submodule.getId()))
            throw new BadRequestException("Submodule already exists");
        submodule.setName(request.name());

        Module module = moduleService.getModuleById(request.moduleId());
        submodule.setModule(module);

        fileService.deleteFile(IMAGE_PATH, submodule.getLogoUrl());
        String logoUrl = fileService.uploadFile(IMAGE_PATH, logo);
        submodule.setLogoUrl(logoUrl);

        submoduleRepository.save(submodule);
    }

    @Override
    public void delete(UUID id) {
        Submodule submodule = getById(id);
        fileService.deleteFile(IMAGE_PATH, submodule.getLogoUrl());
        submoduleRepository.delete(submodule);
    }
}
