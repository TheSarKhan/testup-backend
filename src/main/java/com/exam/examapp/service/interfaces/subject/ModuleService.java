package com.exam.examapp.service.interfaces.subject;

import com.exam.examapp.dto.request.ModuleUpdateRequest;
import com.exam.examapp.dto.response.ModuleResponse;
import com.exam.examapp.model.exam.Module;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface ModuleService {
    void createModule(String moduleName, MultipartFile logo);

    List<Module> getAllModules();

    List<ModuleResponse> getAllModulesResponse();

    Module getModuleById(UUID id);

    void updateModule(ModuleUpdateRequest request, MultipartFile logo);

    void deleteModule(UUID id);
}
