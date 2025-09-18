package com.exam.examapp.service.impl.subject;

import com.exam.examapp.exception.custom.BadRequestException;
import com.exam.examapp.exception.custom.ResourceNotFoundException;
import com.exam.examapp.model.exam.Module;
import com.exam.examapp.repository.subject.ModuleRepository;
import com.exam.examapp.service.impl.LocalFileServiceImpl;
import com.exam.examapp.service.interfaces.subject.ModuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ModuleServiceImpl implements ModuleService {
    private static final String IMAGE_PATH = "uploads/images/modules";

    private final ModuleRepository moduleRepository;

    private final LocalFileServiceImpl fileService;

    @Override
    public void createModule(String moduleName, MultipartFile logo) {
        if (moduleRepository.existsModuleByName(moduleName))
            throw new BadRequestException("Module with name " + moduleName + " already exists.");

        Module build = Module.builder()
                .name(moduleName)
                .build();
        build.setLogoUrl(fileService.uploadFile(IMAGE_PATH, logo));
        moduleRepository.save(build);
    }

    @Override
    public List<Module> getAllModules() {
        return moduleRepository.findAll();
    }

    @Override
    public Module getModuleByName(String moduleName) {
        return moduleRepository.getModuleByName(moduleName).orElseThrow(()->
                new ResourceNotFoundException("Module not found."));
    }

    @Override
    public Module getModuleById(UUID id) {
        return moduleRepository.findById(id).orElseThrow(()->
                new ResourceNotFoundException("Module not found."));
    }

    @Override
    public void updateModule(UUID id, String moduleName, MultipartFile logo) {
        Module module = getModuleById(id);
        Optional<Module> moduleByName = moduleRepository.getModuleByName(moduleName);

        if(moduleByName.isPresent() && !moduleByName.get().getId().equals(id))
            throw new BadRequestException("Module with name " + moduleName + " already exists.");

        module.setName(moduleName);
        fileService.deleteFile(IMAGE_PATH, module.getLogoUrl());
        module.setLogoUrl(fileService.uploadFile(IMAGE_PATH, logo));
        moduleRepository.save(module);
    }

    @Override
    public void deleteModule(UUID id) {
        Module module = getModuleById(id);
        fileService.deleteFile(IMAGE_PATH, module.getLogoUrl());
        moduleRepository.deleteById(id);
    }
}
