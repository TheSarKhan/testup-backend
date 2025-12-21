package com.exam.examapp.dto.response;

import com.exam.examapp.dto.response.subject.QuestionResponse;
import com.exam.examapp.model.enums.AnswerStatus;

import java.util.List;

public record StudentQuestionResponse(
        QuestionResponse response,
        List<String> answers,
        List<Boolean> isAnswerPicture,
        List<AnswerStatus> statuses
) {
}
