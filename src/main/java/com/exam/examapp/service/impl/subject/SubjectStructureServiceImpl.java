package com.exam.examapp.service.impl.subject;

import com.exam.examapp.dto.request.subject.SubjectStructureRequest;
import com.exam.examapp.dto.request.subject.SubjectStructureUpdateRequest;
import com.exam.examapp.exception.custom.ResourceNotFoundException;
import com.exam.examapp.mapper.SubjectStructureMapper;
import com.exam.examapp.model.subject.SubjectStructure;
import com.exam.examapp.repository.subject.SubjectStructureRepository;
import com.exam.examapp.service.interfaces.subject.SubjectService;
import com.exam.examapp.service.interfaces.subject.SubjectStructureService;
import java.util.List;
import java.util.UUID;

import com.exam.examapp.service.interfaces.subject.SubmoduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SubjectStructureServiceImpl implements SubjectStructureService {
  private final SubjectStructureRepository subjectStructureRepository;

  private final SubjectService subjectService;

  private final SubmoduleService submoduleService;

  @Override
  public SubjectStructure create(SubjectStructureRequest request) {
    SubjectStructure subjectStructure = SubjectStructureMapper.requestTo(request);

    subjectStructure.setSubject(subjectService.getById(request.subjectId()));
    subjectStructure.setFree(true);

    if (request.submoduleId() != null) {
        subjectStructure.setSubmodule(submoduleService.getById(request.submoduleId()));
        subjectStructure.setFree(false);
    }

    return subjectStructureRepository.save(subjectStructure);
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
  public SubjectStructure getById(UUID id) {
    return subjectStructureRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Subject structure not found"));
  }

  @Override
  public SubjectStructure update(SubjectStructureUpdateRequest request) {
    SubjectStructure byId = getById(request.id());

    SubjectStructure subjectStructure = SubjectStructureMapper.updateRequestTo(byId, request);

    subjectStructure.setSubject(subjectService.getById(request.request().subjectId()));
    subjectStructure.setFree(true);

    if (request.request().submoduleId() != null) {
        subjectStructure.setSubmodule(submoduleService.getById(request.request().submoduleId()));
        subjectStructure.setFree(false);
    }

    return subjectStructureRepository.save(subjectStructure);
  }

  @Override
  public void delete(UUID id) {
    subjectStructureRepository.deleteById(id);
  }
}
