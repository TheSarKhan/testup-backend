package com.exam.examapp.mapper;

import com.exam.examapp.dto.request.QuestionRequest;
import com.exam.examapp.dto.request.QuestionUpdateRequestForExam;
import com.exam.examapp.model.question.Question;

public class QuestionMapper {
    public static Question requestTo(QuestionRequest request) {
        return Question.builder()
                .title(request.title())
                .titleDescription(request.titleDescription())
                .isTitlePicture(request.isTitlePicture())
                .isTitleContainMath(request.isTitleContainMath())
                .type(request.type())
                .difficulty(request.difficulty())
                .questionCount(request.questionCount())
                .questionDetails(request.questionDetails())
                .build();
    }

    public static QuestionRequest requestToRequest(QuestionUpdateRequestForExam request) {
        return new QuestionRequest(
                request.title(),
                request.titleDescription(),
                request.isTitlePicture(),
                request.isTitleContainMath(),
                request.questionType(),
                request.difficulty(),
                request.topicId(),
                request.questionCount(),
                request.questions(),
                request.questionDetails()
        );
    }

    public static Question updateRequestTo(Question question, QuestionRequest request) {
        question.setTitle(request.title());
        question.setTitleDescription(request.titleDescription());
        question.setTitlePicture(request.isTitlePicture());
        question.setTitleContainMath(request.isTitleContainMath());
        question.setType(request.type());
        question.setDifficulty(request.difficulty());
        question.setQuestionCount(request.questionCount());
        question.setQuestionDetails(request.questionDetails());
        return question;
    }
}
