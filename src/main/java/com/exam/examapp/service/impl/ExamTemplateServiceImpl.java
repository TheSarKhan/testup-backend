package com.exam.examapp.service.impl;

import com.exam.examapp.dto.request.ExamTemplateRequest;
import com.exam.examapp.dto.request.ExamTemplateUpdateRequest;
import com.exam.examapp.dto.request.subject.SubjectStructureUpdateRequest;
import com.exam.examapp.exception.custom.ResourceNotFoundException;
import com.exam.examapp.model.exam.ExamTemplate;
import com.exam.examapp.model.exam.Submodule;
import com.exam.examapp.model.subject.SubjectStructure;
import com.exam.examapp.repository.ExamTemplateRepository;
import com.exam.examapp.service.interfaces.ExamTemplateService;
import com.exam.examapp.service.interfaces.subject.SubjectStructureService;
import com.exam.examapp.service.interfaces.subject.SubmoduleService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExamTemplateServiceImpl implements ExamTemplateService {
  private final ExamTemplateRepository examTemplateRepository;

  private final SubjectStructureService subjectStructureService;

  private final SubmoduleService submoduleService;

  @Override
  public void createExamTemplate(ExamTemplateRequest request) {
    Submodule submodule = submoduleService.getById(request.submoduleId());

    SubjectStructure subjectStructure =
        subjectStructureService.create(request.subjectStructureRequest());

    examTemplateRepository.save(
        ExamTemplate.builder().submodule(submodule).subjectStructure(subjectStructure).build());
  }

  @Override
  public List<ExamTemplate> getAllExamTemplates() {
    return examTemplateRepository.findAll();
  }

  @Override
  public List<ExamTemplate> getExamTemplatesBySubModuleId(UUID submoduleId) {
    return examTemplateRepository.getBySubmodule(submoduleService.getById(submoduleId));
  }

  @Override
  public ExamTemplate getExamTemplateById(UUID id) {
    return examTemplateRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Exam Template not found"));
  }

  @Override
  public void updateExamTemplate(ExamTemplateUpdateRequest request) {
    ExamTemplate examTemplate = getExamTemplateById(request.id());
    Submodule submodule = submoduleService.getById(request.examTemplateRequest().submoduleId());
    SubjectStructure update =
        subjectStructureService.update(
            new SubjectStructureUpdateRequest(
                request.subjectStructureId(),
                request.examTemplateRequest().subjectStructureRequest()));
    examTemplate.setSubmodule(submodule);
    examTemplate.setSubjectStructure(update);
    examTemplateRepository.save(examTemplate);
  }

  @Override
  public void deleteExamTemplate(UUID id) {
    subjectStructureService.delete(getExamTemplateById(id).getSubjectStructure().getId());
    examTemplateRepository.deleteById(id);
  }
}
