package com.exam.examapp.service.impl.subject;

import com.exam.examapp.dto.request.subject.SubjectStructureRequest;
import com.exam.examapp.dto.request.subject.SubjectStructureUpdateRequest;
import com.exam.examapp.exception.custom.BadRequestException;
import com.exam.examapp.exception.custom.ResourceNotFoundException;
import com.exam.examapp.mapper.SubjectStructureMapper;
import com.exam.examapp.model.subject.SubjectStructure;
import com.exam.examapp.repository.subject.SubjectStructureRepository;
import com.exam.examapp.service.interfaces.LogService;
import com.exam.examapp.service.interfaces.UserService;
import com.exam.examapp.service.interfaces.subject.SubjectService;
import com.exam.examapp.service.interfaces.subject.SubjectStructureService;
import com.exam.examapp.service.interfaces.subject.SubmoduleService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubjectStructureServiceImpl implements SubjectStructureService {
    private final SubjectStructureRepository subjectStructureRepository;

    private final SubjectService subjectService;

    private final SubmoduleService submoduleService;

    private final UserService userService;

    private final LogService logService;

    @Override
    @Transactional
    public SubjectStructure create(SubjectStructureRequest request) {
        log.info("Mövzu strukturu yaradılır");
        SubjectStructure subjectStructure = SubjectStructureMapper.requestTo(request);

        subjectStructure.setSubject(subjectService.getById(request.subjectId()));
        subjectStructure.setFree(true);
        subjectStructure.setActive(true);

        if (request.submoduleId() != null) {
            if (existsBySubmoduleAndSubjectId(request.submoduleId(), request.subjectId()))
                throw new BadRequestException("Mövzu strukturu artıq mövcuddur");
            subjectStructure.setSubmodule(submoduleService.getById(request.submoduleId()));
            subjectStructure.setFree(false);
        }

        SubjectStructure save = subjectStructureRepository.save(subjectStructure);
        log.info("Mövzu strukturu yaradılıdı");
        logService.save("Mövzu strukturu yaradılıdı", userService.getCurrentUserOrNull());
        return save;
    }

    @Override
    public List<SubjectStructure> getAll() {
        return subjectStructureRepository.findByIsActive(true);
    }

    @Override
    public List<SubjectStructure> getBySubjectId(UUID subjectId) {
        return subjectStructureRepository.getBySubject_IdAndIsActive(subjectId, true);
    }

    @Override
    public List<SubjectStructure> getBySubmoduleId(UUID submoduleId) {
        return subjectStructureRepository.getBySubmodule_IdAndIsActive(submoduleId, true);
    }

    @Override
    public List<SubjectStructure> getFreeStructures() {
        return subjectStructureRepository.getBySubmodule_IdAndIsActive(null, true);
    }

    @Override
    public List<SubjectStructure> getStructuredStructures() {
        return subjectStructureRepository.getBySubmoduleIsNotNullAndIsActive(true);
    }

    @Override
    public SubjectStructure getBySubmoduleAndSubjectId(UUID submoduleId, UUID subjectId) {
        log.info("Mövcud struktur götürülür. Alt modul id: {}, fənn id:{}", submoduleId, subjectId);
        return subjectStructureRepository.getBySubmodule_IdAndSubject_IdAndIsActive(submoduleId, subjectId, true)
                .orElseThrow(() -> new ResourceNotFoundException("Mövzu strukturu tapılmadı"));
    }

    @Override
    public boolean existsBySubmoduleAndSubjectId(UUID submoduleId, UUID subjectId) {
        return subjectStructureRepository.existsBySubmodule_IdAndSubject_IdAndIsActive(
                submoduleId, subjectId, true);
    }

    @Override
    public SubjectStructure getById(UUID id) {
        return subjectStructureRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Mövzu strukturu tapılmadı"));
    }

    @Override
    @Transactional
    public SubjectStructure update(SubjectStructureUpdateRequest request) {
        log.info("Mövzu strukturu yenilənir");
        deactivate(request.id());

        return create(request.request());
    }

    @Override
    public void delete(UUID id) {
        log.info("Mövzu strukturu silinir");
        deactivate(id);
    }

    private void deactivate(UUID id) {
        SubjectStructure subjectStructure = getById(id);

        subjectStructure.setActive(false);
        subjectStructureRepository.save(subjectStructure);
        log.info("Mövzu strukturu deactiv edildi");
        logService.save("Mövzu strukturu deactiv edildi", userService.getCurrentUserOrNull());
    }
}
