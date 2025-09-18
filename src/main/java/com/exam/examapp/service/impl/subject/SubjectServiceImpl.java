package com.exam.examapp.service.impl.subject;

import com.exam.examapp.exception.custom.BadRequestException;
import com.exam.examapp.exception.custom.ResourceNotFoundException;
import com.exam.examapp.model.subject.Subject;
import com.exam.examapp.repository.subject.SubjectRepository;
import com.exam.examapp.service.interfaces.FileService;
import com.exam.examapp.service.interfaces.subject.SubjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SubjectServiceImpl implements SubjectService {
    private static final String IMAGE_PATH= "uploads/images/subjects";

    private final SubjectRepository subjectRepository;

    private final FileService fileService;

    @Override
    public void save(String name, MultipartFile logo) {
        if (subjectRepository.existsSubjectByName(name))
            throw new BadRequestException("Subject already exists");

        Subject build = Subject.builder().name(name).build();
        String logoUrl = fileService.uploadFile(IMAGE_PATH, logo);
        build.setLogoUrl(logoUrl);
        subjectRepository.save(build);
    }

    @Override
    public List<Subject> getAll() {
        return subjectRepository.findAll();
    }

    @Override
    public Subject getByName(String name) {
        return subjectRepository.getByName(name).orElseThrow(()->
                new ResourceNotFoundException("Subject not found"));
    }

    @Override
    public Subject getById(UUID id) {
        return subjectRepository.findById(id).orElseThrow(()->
                new ResourceNotFoundException("Subject not found"));
    }

    @Override
    public void update(UUID id, String name, MultipartFile logo) {
        Subject subject = getById(id);
        Optional<Subject> byName = subjectRepository.getByName(name);
        if (byName.isPresent() && !byName.get().getId().equals(id))
            throw new BadRequestException("Subject Already Exists");

        subject.setName(name);
        fileService.deleteFile(IMAGE_PATH, subject.getLogoUrl());
        String logoUrl = fileService.uploadFile(IMAGE_PATH, logo);
        subject.setLogoUrl(logoUrl);
        subjectRepository.save(subject);
    }

    @Override
    public void delete(UUID id) {
        Subject subject = getById(id);
        fileService.deleteFile(IMAGE_PATH, subject.getLogoUrl());
        subjectRepository.delete(subject);
    }
}
