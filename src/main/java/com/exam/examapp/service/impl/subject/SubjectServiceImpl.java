package com.exam.examapp.service.impl.subject;

import com.exam.examapp.dto.request.subject.SubjectRequest;
import com.exam.examapp.exception.custom.BadRequestException;
import com.exam.examapp.exception.custom.ResourceNotFoundException;
import com.exam.examapp.model.subject.Subject;
import com.exam.examapp.repository.subject.SubjectRepository;
import com.exam.examapp.service.interfaces.FileService;
import com.exam.examapp.service.interfaces.LogService;
import com.exam.examapp.service.interfaces.UserService;
import com.exam.examapp.service.interfaces.subject.SubjectService;
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
public class SubjectServiceImpl implements SubjectService {
    private static final String IMAGE_PATH = "uploads/images/subjects";

    private final SubjectRepository subjectRepository;

    private final UserService userService;

    private final FileService fileService;

    private final LogService logService;

    @Override
    public void save(SubjectRequest request, MultipartFile logo) {
        log.info("Mövzu yaradılır");
        if (subjectRepository.existsSubjectByName(request.name()))
            throw new BadRequestException("Mövzu artıq mövcuddur");

        Subject build = Subject.builder()
                .name(request.name())
                .isSupportMath(request.isSupportMath())
                .build();

        String logoUrl = fileService.uploadFile(IMAGE_PATH, logo);
        build.setLogoUrl(logoUrl);
        subjectRepository.save(build);
        log.info("Mövzu yaradıldı");
        logService.save("Mövzu yaradıldı", userService.getCurrentUserOrNull());
    }

    @Override
    public List<Subject> getAll() {
        return subjectRepository.findAll();
    }

    @Override
    public Subject getByName(String name) {
        return subjectRepository.getByName(name).orElseThrow(() ->
                new ResourceNotFoundException("Mövzu tapılmadı"));
    }

    @Override
    public Subject getById(UUID id) {
        return subjectRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Mövzu tapılmadı"));
    }

    @Override
    public void update(UUID id, String name, boolean isSupportMath, MultipartFile logo) {
        log.info("Mövzu yenilənir");
        Subject subject = getById(id);
        Optional<Subject> byName = subjectRepository.getByName(name);
        if (byName.isPresent() && !byName.get().getId().equals(id))
            throw new BadRequestException("Mövzu artıq mövcuddur");

        subject.setName(name);
        subject.setSupportMath(isSupportMath);
        fileService.deleteFile(IMAGE_PATH, subject.getLogoUrl());
        String logoUrl = fileService.uploadFile(IMAGE_PATH, logo);
        subject.setLogoUrl(logoUrl);
        subjectRepository.save(subject);
        log.info("Mövzu yeniləndi");
        logService.save("Mövzu yeniləndi", userService.getCurrentUserOrNull());
    }

    @Override
    public void delete(UUID id) {
        log.info("Mövzu silinir");
        Subject subject = getById(id);
        fileService.deleteFile(IMAGE_PATH, subject.getLogoUrl());
        subjectRepository.delete(subject);
        log.info("Mövzu silindi");
        logService.save("Mövzu silindi", userService.getCurrentUserOrNull());
    }
}
