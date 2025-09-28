package com.exam.examapp.service.interfaces.exam;

import com.exam.examapp.dto.response.exam.StartExamResponse;
import com.exam.examapp.dto.response.exam.statistic.ExamStatistics;
import com.exam.examapp.model.enums.AnswerStatus;

import java.util.UUID;

public interface ExamCheckService {
    ExamStatistics getExamStatistics(UUID id);

    StartExamResponse getUserExam(UUID studentExamId);

    void checkAnswer(UUID studentExamId, UUID questionId, AnswerStatus status);
}
