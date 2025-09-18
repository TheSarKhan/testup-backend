package com.exam.examapp.service.interfaces.information;

import com.exam.examapp.dto.request.information.SuperiorityRequest;
import com.exam.examapp.dto.request.information.SuperiorityUpdateRequest;
import com.exam.examapp.model.enums.SuperiorityType;
import com.exam.examapp.model.information.Superiority;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface SuperiorityService {
    void createSuperiority(SuperiorityRequest request, MultipartFile icon);

    List<Superiority> getAllSuperiority();

    List<Superiority> getSuperiorityBySuperiorityType(SuperiorityType type);

    Superiority getSuperiorityById(UUID id);

    void updateSuperiority(SuperiorityUpdateRequest request, MultipartFile icon);

    void deleteSuperiority(UUID id);
}
