package com.exam.examapp.service.interfaces.subject;

import com.exam.examapp.model.subject.Subject;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface SubjectService {
    void save(String name, MultipartFile logo);

    List<Subject> getAll();

    Subject getByName(String name);

    Subject getById(UUID id);

    void update(UUID id, String name, MultipartFile logo);

    void delete(UUID id);
}
