package com.exam.examapp.dto.request;

import com.exam.examapp.dto.request.subject.SubjectStructureUpdateRequest;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record SubjectStructureQuestionsUpdateRequest(
    @NotNull SubjectStructureUpdateRequest subjectStructureUpdateRequest,
    @NotNull List<QuestionUpdateRequestForExam> questionRequests) {}
