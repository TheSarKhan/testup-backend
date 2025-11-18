package com.exam.examapp.service.impl.subject;

import com.exam.examapp.dto.request.ModuleUpdateRequest;
import com.exam.examapp.dto.response.subject.ModuleResponse;
import com.exam.examapp.exception.custom.BadRequestException;
import com.exam.examapp.exception.custom.ResourceNotFoundException;
import com.exam.examapp.model.exam.Module;
import com.exam.examapp.repository.ExamRepository;
import com.exam.examapp.repository.subject.ModuleRepository;
import com.exam.examapp.repository.subject.SubjectStructureQuestionRepository;
import com.exam.examapp.repository.subject.SubjectStructureRepository;
import com.exam.examapp.repository.subject.SubmoduleRepository;
import com.exam.examapp.service.impl.LocalFileServiceImpl;
import com.exam.examapp.service.interfaces.LogService;
import com.exam.examapp.service.interfaces.UserService;
import com.exam.examapp.service.interfaces.subject.ModuleService;
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
public class ModuleServiceImpl implements ModuleService {
    private static final String IMAGE_PATH = "uploads/images/modules";

    private final ModuleRepository moduleRepository;

    private final UserService userService;

    private final LocalFileServiceImpl fileService;

    private final LogService logService;

    private final SubmoduleRepository submoduleRepository;

    private final ExamRepository examRepository;

    private final SubjectStructureRepository subjectStructureRepository;

    private final SubjectStructureQuestionRepository subjectStructureQuestionRepository;

    @Override
    public void createModule(String moduleName, MultipartFile logo) {
        log.info("Modul yaradılır");
        if (moduleRepository.existsModuleByName(moduleName))
            throw new BadRequestException(moduleName + " adlı modul artıq mövcuddur.");

        Module build = buildModule(moduleName);
        build.setLogoUrl(fileService.uploadFile(IMAGE_PATH, logo));
        moduleRepository.save(build);
        log.info("Modul yaradıldı");
        logService.save("Modul yaradıldı", userService.getCurrentUserOrNull());
    }

    private static Module buildModule(String moduleName) {
        return Module.builder()
                .name(moduleName)
                .build();
    }

    @Override
    public List<Module> getAllModules() {
        return moduleRepository.findAll();
    }

    @Override
    @Transactional
    public List<ModuleResponse> getAllModulesResponse() {
        return moduleRepository.getAllModuleResponses();
    }

    @Override
    public Module getModuleById(UUID id) {
        return moduleRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Modul tapılmadı"));
    }

    @Override
    public void updateModule(ModuleUpdateRequest request, MultipartFile logo) {
        log.info("Modul yenilənir");
        Module module = getModuleById(request.id());
        Optional<Module> moduleByName = moduleRepository.getModuleByName(request.moduleName());

        if (moduleByName.isPresent() && !moduleByName.get().getId().equals(request.id()))
            throw new BadRequestException(request.moduleName() + " adlı modul artıq mövcuddur.");

        module.setName(request.moduleName());
        if (logo != null) {
            fileService.deleteFile(IMAGE_PATH, module.getLogoUrl());
            module.setLogoUrl(fileService.uploadFile(IMAGE_PATH, logo));
        }
        moduleRepository.save(module);
        log.info("Modul yeniləndi");
        logService.save("Modul yeniləndi", userService.getCurrentUserOrNull());
    }

    @Override
    public void deleteModule(UUID id) {
        log.info("Modul silinir");
        Module module = getModuleById(id);
        fileService.deleteFile(IMAGE_PATH, module.getLogoUrl());
        moduleRepository.deleteById(id);
        log.info("Modul silindi");
        logService.save("Modul silindi", userService.getCurrentUserOrNull());
    }
}
