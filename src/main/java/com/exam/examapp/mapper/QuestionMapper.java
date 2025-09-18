package com.exam.examapp.mapper;

import com.exam.examapp.dto.request.QuestionRequest;
import com.exam.examapp.model.question.Question;

public class QuestionMapper {
    public static Question requestTo(QuestionRequest request) {
        return Question.builder()
                .title(request.title())
                .titleDescription(request.titleDescription())
                .isTitlePicture(request.isTitlePicture())
                .type(request.questionType())
                .difficulty(request.difficulty())
                .questionCount(request.questionCount())
                .questionDetails(request.questionDetails())
                .build();
    }

    public static Question updateRequestTo(Question question, QuestionRequest request) {
        question.setTitle(request.title());
        question.setTitleDescription(request.titleDescription());
        question.setTitlePicture(request.isTitlePicture());
        question.setType(request.questionType());
        question.setDifficulty(request.difficulty());
        question.setQuestionCount(request.questionCount());
        question.setQuestionDetails(request.questionDetails());
        return question;
    }
}
