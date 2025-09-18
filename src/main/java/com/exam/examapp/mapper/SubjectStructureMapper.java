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
        SubjectStructureRequest request1 = request.request();
        subjectStructure.setQuestionCount(request1.questionCount());
        subjectStructure.setQuestionTypeCountMap(request1.questionTypeCountMap());
        subjectStructure.setQuestionToPointMap(request1.questionToPointMap());
        subjectStructure.setTextListeningQuestionToCountMap(request1.textListeningQuestionToCountMap());
        subjectStructure.setFormula(request1.formula());
        return subjectStructure;
    }
}
