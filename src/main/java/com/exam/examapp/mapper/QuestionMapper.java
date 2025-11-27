package com.exam.examapp.mapper;

import com.exam.examapp.dto.request.QuestionRequest;
import com.exam.examapp.dto.request.QuestionUpdateRequest;
import com.exam.examapp.dto.request.QuestionUpdateRequestForExam;
import com.exam.examapp.model.question.Question;

public class QuestionMapper {
    public static Question requestTo(QuestionRequest request) {
        return Question.builder()
                .title(request.title())
                .titleDescription(request.titleDescription())
                .isTitlePicture(request.isTitlePicture())
                .mathTitle(request.mathTitle())
                .type(request.type())
                .difficulty(request.difficulty())
                .questionCount(request.questionCount())
                .questionDetails(request.questionDetails())
                .build();
    }

    public static QuestionUpdateRequest requestToRequest(QuestionUpdateRequestForExam request) {
        return new QuestionUpdateRequest(
                request.id(),
                request.title(),
                request.titleDescription(),
                request.isTitlePicture(),
                request.mathTitle(),
                request.questionType(),
                request.difficulty(),
                request.topicId(),
                request.questionCount(),
                request.questions(),
                request.soundUrl(),
                request.questionDetails()
        );
    }

    public static Question updateRequestTo(Question question, QuestionUpdateRequest request) {
        question.setTitle(request.title());
        question.setTitleDescription(request.titleDescription());
        question.setTitlePicture(request.isTitlePicture());
        question.setMathTitle(request.mathTitle());
        question.setType(request.type());
        question.setDifficulty(request.difficulty());
        question.setQuestionCount(request.questionCount());
        return question;
    }
}
