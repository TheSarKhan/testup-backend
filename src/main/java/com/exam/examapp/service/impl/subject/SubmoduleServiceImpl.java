package com.exam.examapp.service.impl.subject;

import com.exam.examapp.dto.request.subject.SubmoduleRequest;
import com.exam.examapp.dto.request.subject.SubmoduleUpdateRequest;
import com.exam.examapp.dto.response.subject.SubmoduleProjection;
import com.exam.examapp.dto.response.subject.SubmoduleResponse;
import com.exam.examapp.exception.custom.BadRequestException;
import com.exam.examapp.exception.custom.ResourceNotFoundException;
import com.exam.examapp.model.exam.Module;
import com.exam.examapp.model.exam.Submodule;
import com.exam.examapp.repository.subject.SubmoduleRepository;
import com.exam.examapp.service.interfaces.FileService;
import com.exam.examapp.service.interfaces.LogService;
import com.exam.examapp.service.interfaces.UserService;
import com.exam.examapp.service.interfaces.subject.ModuleService;
import com.exam.examapp.service.interfaces.subject.SubmoduleService;
import jakarta.transaction.Transactional;
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
public class SubmoduleServiceImpl implements SubmoduleService {
    private static final String IMAGE_PATH = "uploads/images/sub-modules";

    private final SubmoduleRepository submoduleRepository;

    private final UserService userService;

    private final ModuleService moduleService;

    private final FileService fileService;

    private final LogService logService;

    private static Submodule buildSubmodule(SubmoduleRequest request, Module module) {
        return Submodule.builder()
                .name(request.name())
                .module(module)
                .build();
    }

    @Override
    public void create(SubmoduleRequest request, MultipartFile logo) {
        log.info("Alt modul yaradılır");
        Module module = moduleService.getModuleById(request.moduleId());
        if (submoduleRepository.existsByNameAndModule(request.name(), module))
            throw new BadRequestException("Alt modul artıq mövcuddur.");

        Submodule build = buildSubmodule(request, module);

        String logoUrl = fileService.uploadFile(IMAGE_PATH, logo);
        build.setLogoUrl(logoUrl);
        submoduleRepository.save(build);
        log.info("Alt modul yaradıldı");
        logService.save("Alt modul yaradıldı", userService.getCurrentUserOrNull());
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
    @Transactional
    public List<SubmoduleResponse> getAllSubmoduleResponse() {
        return submoduleRepository.getAllNative().stream()
                .map(SubmoduleProjection::toSubmoduleResponse).toList();
    }

    @Override
    public Submodule getById(UUID id) {
        return submoduleRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Alt modul tapılmadı"));
    }

    @Override
    public Submodule getByName(String name) {
        return submoduleRepository.getByName(name).orElseThrow(() ->
                new ResourceNotFoundException("Alt modul tapılmadı"));
    }

    @Override
    public void update(SubmoduleUpdateRequest request, MultipartFile logo) {
        log.info("Alt modul yenilənir");
        Submodule submodule = getById(request.id());
        Optional<Submodule> byName = submoduleRepository.getByName(request.name());
        if (byName.isPresent() && !byName.get().getId().equals(submodule.getId()))
            throw new BadRequestException("Alt modul artıq mövcuddur");
        submodule.setName(request.name());

        Module module = moduleService.getModuleById(request.moduleId());
        submodule.setModule(module);

        if (logo != null) {
            fileService.deleteFile(IMAGE_PATH, submodule.getLogoUrl());
            String logoUrl = fileService.uploadFile(IMAGE_PATH, logo);
            submodule.setLogoUrl(logoUrl);
        }

        submoduleRepository.save(submodule);
        log.info("Alt modul yeniləndi");
        logService.save("Alt modul yeniləndi", userService.getCurrentUserOrNull());
    }

    @Override
    public void delete(UUID id) {
        log.info("Alt modul silinir");
        Submodule submodule = getById(id);
        fileService.deleteFile(IMAGE_PATH, submodule.getLogoUrl());
        submoduleRepository.delete(submodule);
        log.info("Alt modul silindi");
        logService.save("Alt modul silindi", userService.getCurrentUserOrNull());
    }
}
