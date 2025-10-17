package com.exam.examapp.service.interfaces.subject;

import com.exam.examapp.dto.request.subject.SubmoduleRequest;
import com.exam.examapp.dto.request.subject.SubmoduleUpdateRequest;
import com.exam.examapp.dto.response.SubmoduleResponse;
import com.exam.examapp.model.exam.Submodule;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface SubmoduleService {
    void create(SubmoduleRequest request, MultipartFile logo);

    List<Submodule> getAll();

    List<Submodule> getAllByModule(UUID moduleId);

    List<SubmoduleResponse> getAllSubmoduleResponse();

    Submodule getById(UUID id);

    Submodule getByName(String name);

    void update(SubmoduleUpdateRequest request, MultipartFile logo);

    void delete(UUID id);
}
