package com.exam.examapp.service.interfaces.exam;

import com.exam.examapp.dto.request.ExamTemplateRequest;
import com.exam.examapp.dto.request.ExamTemplateUpdateRequest;
import com.exam.examapp.model.exam.ExamTemplate;

import java.util.List;
import java.util.UUID;

public interface ExamTemplateService {
    void createExamTemplate(ExamTemplateRequest request);

    List<ExamTemplate> getAllExamTemplates();

    List<ExamTemplate> getExamTemplatesBySubModuleId(UUID submoduleId);

    ExamTemplate getExamTemplateById(UUID id);

    void updateExamTemplate(ExamTemplateUpdateRequest request);

    void deleteExamTemplate(UUID id);
}
