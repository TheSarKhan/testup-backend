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
    public SubjectStructure create(SubjectStructureRequest request) {
        log.info("Mövzu strukturu yaradılır");
        SubjectStructure subjectStructure = SubjectStructureMapper.requestTo(request);

        subjectStructure.setSubject(subjectService.getById(request.subjectId()));
        subjectStructure.setFree(true);

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
        return subjectStructureRepository.findAll();
    }

    @Override
    public List<SubjectStructure> getBySubjectId(UUID subjectId) {
        return subjectStructureRepository.getBySubject_Id(subjectId);
    }

    @Override
    public List<SubjectStructure> getBySubmoduleId(UUID submoduleId) {
        return subjectStructureRepository.getBySubmodule_Id(submoduleId);
    }

    @Override
    public SubjectStructure getBySubmoduleAndSubjectId(UUID submoduleId, UUID subjectId) {
        log.info("Mövcud struktur götürülür. Alt modul id: {}, fənn id:{}", submoduleId, subjectId);
        return subjectStructureRepository.getBySubmodule_IdAndSubject_Id(submoduleId, subjectId).orElseThrow(() ->
                new ResourceNotFoundException("Mövzu strukturu tapılmadı"));
    }

    @Override
    public boolean existsBySubmoduleAndSubjectId(UUID submoduleId, UUID subjectId) {
        return subjectStructureRepository.existsBySubmodule_IdAndSubject_Id(submoduleId, subjectId);
    }

    @Override
    public SubjectStructure getById(UUID id) {
        return subjectStructureRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Mövzu strukturu tapılmadı"));
    }

    @Override
    public SubjectStructure update(SubjectStructureUpdateRequest request) {
        log.info("Mövzu strukturu yenilənir");
        SubjectStructure byId = getById(request.id());

        SubjectStructure subjectStructure = SubjectStructureMapper.updateRequestTo(byId, request);

        subjectStructure.setSubject(subjectService.getById(request.request().subjectId()));
        subjectStructure.setFree(true);

        if (request.request().submoduleId() != null) {
            if (existsBySubmoduleAndSubjectId(
                    request.request().submoduleId(),
                    request.request().subjectId()))
                throw new BadRequestException("Mövzu strukturu artıq mövcuddur");
            subjectStructure.setSubmodule(submoduleService.getById(request.request().submoduleId()));
            subjectStructure.setFree(false);
        }

        SubjectStructure save = subjectStructureRepository.save(subjectStructure);
        log.info("Mövzu strukturu yeniləndi");
        logService.save("Mövzu strukturu yeniləndi", userService.getCurrentUserOrNull());
        return save;
    }

    @Override
    public void delete(UUID id) {
        log.info("Mövzu strukturu silinir");
        subjectStructureRepository.deleteById(id);
        log.info("Mövzu strukturu silindi");
        logService.save("Mövzu strukturu silindi", userService.getCurrentUserOrNull());
    }
}
