package com.exam.examapp.service.impl.exam.helper;

import com.exam.examapp.dto.request.QuestionRequest;
import com.exam.examapp.dto.request.SubjectStructureQuestionsRequest;
import com.exam.examapp.dto.request.exam.ExamRequest;
import com.exam.examapp.dto.request.subject.SubjectStructureRequest;
import com.exam.examapp.exception.custom.BadRequestException;
import com.exam.examapp.exception.custom.ReachedLimitException;
import com.exam.examapp.model.User;
import com.exam.examapp.model.enums.QuestionType;
import com.exam.examapp.model.enums.Role;

import java.util.List;
import java.util.Optional;

public class ExamValidationService {
    public static void validateExam(
            User user,
            List<QuestionRequest> questionRequests,
            boolean hasPicture,
            boolean hasPdfPicture,
            boolean hasSound,
            boolean hidden,
            int size,
            Integer integer) {
        Optional<QuestionRequest> fistManualCheckQuestion =
                questionRequests.stream()
                        .filter(
                                questionRequest ->
                                        QuestionType.OPEN_ENDED.equals(questionRequest.questionType())
                                                && (questionRequest.questionDetails().isAuto() != null
                                                && !questionRequest.questionDetails().isAuto()))
                        .findFirst();

        if (!user.getPack().isCanAddPicture() && hasPicture)
            throw new BadRequestException("You cannot add picture for this exam.");

        if (!user.getPack().isCanAddPdfSound() && (hasPdfPicture || hasSound))
            throw new BadRequestException("You cannot add sound or pdf picture for this exam.");

        if (hidden && !user.getPack().isCanShareViaCode())
            throw new BadRequestException(
                    "You cannot share this exam via code (cannot create hidden exam).");

        if (!user.getPack().isCanAddMultipleSubjectInOneExam() && size > 1)
            throw new BadRequestException("You cannot add more than one subject in one exam.");

        if (!user.getPack().isCanAddManualCheckAutoQuestion() && fistManualCheckQuestion.isPresent())
            throw new BadRequestException("You cannot add manual check question in this exam.");

        if (integer != null && !user.getPack().isCanSelectExamDuration())
            throw new BadRequestException("You cannot select exam duration.");
    }

    public static void validateRequest(ExamRequest request, User user) {
        if (Role.TEACHER.equals(user.getRole())
                && (user.getInfo().getThisMonthCreatedExamCount() >= user.getPack().getMonthlyExamCount()
                || user.getInfo().getCurrentlyTotalExamCount() >= user.getPack().getTotalExamCount()))
            throw new ReachedLimitException(
                    "You have reached the limit of exams for this month or total");

        Integer questionCountTotal =
                request.subjectStructures().stream()
                        .map(SubjectStructureQuestionsRequest::subjectStructureRequest)
                        .map(SubjectStructureRequest::questionCount)
                        .reduce(Integer::sum)
                        .orElse(0);

        if (questionCountTotal >= user.getPack().getQuestionCountPerExam())
            throw new ReachedLimitException("You have reached the limit of questions for this exam");

        List<QuestionRequest> questionRequests =
                request.subjectStructures().stream()
                        .map(SubjectStructureQuestionsRequest::questionRequests)
                        .reduce(
                                (a, b) -> {
                                    a.addAll(b);
                                    return a;
                                })
                        .orElseThrow(() -> new BadRequestException("Questions cannot be empty."));

        ExamValidationService.validateExam(
                user,
                questionRequests,
                request.hasPicture(),
                request.hasPdfPicture(),
                request.hasSound(),
                request.isHidden(),
                request.subjectStructures().size(),
                request.durationInSeconds());
    }
}
