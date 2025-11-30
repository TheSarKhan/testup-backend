package com.exam.examapp.service.interfaces.subject;

import com.exam.examapp.dto.request.subject.SubjectStructureRequest;
import com.exam.examapp.dto.request.subject.SubjectStructureUpdateRequest;
import com.exam.examapp.model.subject.SubjectStructure;

import java.util.List;
import java.util.UUID;

public interface SubjectStructureService {
    SubjectStructure create(SubjectStructureRequest request);

    SubjectStructure create(SubjectStructureUpdateRequest request);

    List<SubjectStructure> getAll();

    List<SubjectStructure> getBySubjectId(UUID subjectId);

    List<SubjectStructure> getBySubmoduleId(UUID submoduleId);

    List<SubjectStructure> getFreeStructures();

    List<SubjectStructure> getStructuredStructures();

    SubjectStructure getBySubmoduleAndSubjectId(UUID submoduleId, UUID subjectId);

    boolean existsBySubmoduleAndSubjectId(UUID submoduleId, UUID subjectId);

    SubjectStructure getById(UUID id);

    SubjectStructure update(SubjectStructureUpdateRequest request);

    void delete(UUID id);
}
