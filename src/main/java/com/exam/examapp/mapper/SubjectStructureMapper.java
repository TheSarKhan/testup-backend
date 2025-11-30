package com.exam.examapp.mapper;

import com.exam.examapp.dto.request.subject.SubjectStructureRequest;
import com.exam.examapp.dto.request.subject.SubjectStructureUpdateRequest;
import com.exam.examapp.model.subject.SubjectStructure;

public class SubjectStructureMapper {
    public static SubjectStructure requestTo(SubjectStructureRequest request) {
        return SubjectStructure.builder()
                .questionCount(request.questionCount())
                .questionTypeCountMap(request.questionTypeCountMap())
                .questionToPointMap(request.questionToPointMap())
                .textListeningQuestionToCountMap(request.textListeningQuestionToCountMap())
                .formula(request.formula())
                .build();
    }

    public static SubjectStructure updateRequestTo(SubjectStructure subjectStructure,
                                                   SubjectStructureUpdateRequest request) {
        subjectStructure.setQuestionCount(request.questionCount());
        subjectStructure.setQuestionTypeCountMap(request.questionTypeCountMap());
        subjectStructure.setQuestionToPointMap(request.questionToPointMap());
        subjectStructure.setTextListeningQuestionToCountMap(request.textListeningQuestionToCountMap());
        subjectStructure.setFormula(request.formula());
        return subjectStructure;
    }
}
