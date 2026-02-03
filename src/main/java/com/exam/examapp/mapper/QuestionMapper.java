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
                .isTitleContainMath(request.isTitleContainMath())
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
                request.isTitleContainMath(),
                request.type(),
                request.difficulty(),
                request.topicId(),
                request.questionCount(),
                request.questions(),
                request.soundUrl(),
                request.questionDetails(),
                request.questionDbId()
        );
    }

    public static Question updateRequestTo(Question question, QuestionUpdateRequest request) {
        question.setTitle(request.title());
        question.setTitleDescription(request.titleDescription());
        question.setTitlePicture(request.isTitlePicture());
        question.setTitleContainMath(request.isTitleContainMath());
        question.setType(request.type());
        question.setDifficulty(request.difficulty());
        question.setQuestionCount(request.questionCount());
        return question;
    }

    public static QuestionUpdateRequest requestToUpdateRequest(QuestionRequest request) {
        return new QuestionUpdateRequest(
                null,
                request.title(),
                request.titleDescription(),
                request.isTitlePicture(),
                request.isTitleContainMath(),
                request.type(),
                request.difficulty(),
                request.topicId(),
                request.questionCount(),
                request.questions() != null ? request.questions().stream()
                        .map(QuestionMapper::requestToUpdateRequest).toList() : null,
                request.soundUrl(),
                request.questionDetails(),
                request.questionDbId()
        );
    }

    public static QuestionRequest updateRequestToRequest(QuestionUpdateRequestForExam request) {
        return new QuestionRequest(
                request.title(),
                request.titleDescription(),
                request.isTitlePicture(),
                request.isTitleContainMath(),
                request.type(),
                request.difficulty(),
                request.topicId(),
                request.questionCount(),
                request.questions() != null ? request.questions().stream()
                        .map(QuestionMapper::updateRequestToRequest).toList() : null,
                request.soundUrl(),
                request.questionDetails(),
                request.questionDbId()
        );
    }

    public static QuestionRequest updateRequestToRequest(QuestionUpdateRequest request) {
        return new QuestionRequest(
                request.title(),
                request.titleDescription(),
                request.isTitlePicture(),
                request.isTitleContainMath(),
                request.type(),
                request.difficulty(),
                request.topicId(),
                request.questionCount(),
                request.questions() != null ? request.questions().stream()
                        .map(QuestionMapper::updateRequestToRequest).toList() : null,
                request.soundUrl(),
                request.questionDetails(),
                request.questionDbId()
        );
    }
}
