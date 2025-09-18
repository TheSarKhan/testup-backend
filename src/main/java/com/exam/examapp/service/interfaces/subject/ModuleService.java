package com.exam.examapp.service.interfaces.subject;

import com.exam.examapp.model.exam.Module;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface ModuleService {
    void createModule(String moduleName, MultipartFile logo);

    List<Module> getAllModules();

    Module getModuleByName(String moduleName);

    Module getModuleById(UUID id);

    void updateModule(UUID id, String moduleName, MultipartFile logo);

    void deleteModule(UUID id);
}
