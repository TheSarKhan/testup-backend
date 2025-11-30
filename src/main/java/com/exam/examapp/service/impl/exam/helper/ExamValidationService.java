package com.exam.examapp.service.impl.exam.helper;

import com.exam.examapp.dto.request.QuestionRequest;
import com.exam.examapp.dto.request.QuestionUpdateRequestForExam;
import com.exam.examapp.dto.request.SubjectStructureQuestionsRequest;
import com.exam.examapp.dto.request.SubjectStructureQuestionsUpdateRequest;
import com.exam.examapp.dto.request.exam.ExamRequest;
import com.exam.examapp.dto.request.exam.ExamUpdateRequest;
import com.exam.examapp.dto.request.subject.SubjectStructureRequest;
import com.exam.examapp.dto.request.subject.SubjectStructureUpdateRequest;
import com.exam.examapp.exception.custom.BadRequestException;
import com.exam.examapp.exception.custom.ReachedLimitException;
import com.exam.examapp.model.User;
import com.exam.examapp.model.enums.QuestionType;
import com.exam.examapp.model.enums.Role;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

@Slf4j
public class ExamValidationService {
    private static void validateExam(
            User user,
            List<QuestionRequest> questionRequests,
            boolean hasPicture,
            boolean hasPdfPicture,
            boolean hasSound,
            boolean hidden,
            int size,
            Integer integer) {
        log.info("Validasiya davam edir");
        Optional<QuestionRequest> fistManualCheckQuestion =
                questionRequests.stream()
                        .filter(
                                questionRequest ->
                                        QuestionType.OPEN_ENDED.equals(questionRequest.type())
                                                && (questionRequest.questionDetails().isAuto() != null
                                                && !questionRequest.questionDetails().isAuto()))
                        .findFirst();

        if (!user.getPack().isCanAddManualCheckAutoQuestion() && fistManualCheckQuestion.isPresent())
            throw new BadRequestException("Bu imtahana əl ilə yoxlama sualı əlavə edə bilməzsiniz");

        validatePack(user, hasPicture, hasPdfPicture, hasSound, hidden, size, integer);
    }

    public static void validateRequest(ExamRequest request, User user) {
        log.info("Validasiya başladı");
        if (Role.TEACHER.equals(user.getRole())
                && (user.getInfo().getThisMonthCreatedExamCount() >= user.getPack().getMonthlyExamCount()
                || user.getInfo().getCurrentlyTotalExamCount() >= user.getPack().getTotalExamCount()))
            throw new ReachedLimitException(
                    "Bu ay və ya cəmi imtahan limitinə çatmısınız");

        Integer questionCountTotal =
                request.subjectStructures().stream()
                        .map(SubjectStructureQuestionsRequest::subjectStructureRequest)
                        .map(SubjectStructureRequest::questionCount)
                        .reduce(Integer::sum)
                        .orElse(0);

        if (questionCountTotal >= user.getPack().getQuestionCountPerExam())
            throw new ReachedLimitException("Bu imtahan üçün suallar limitinə çatdınız");

        List<QuestionRequest> questionRequests =
                request.subjectStructures().stream()
                        .map(SubjectStructureQuestionsRequest::questionRequests)
                        .reduce(
                                (a, b) -> {
                                    a.addAll(b);
                                    return a;
                                })
                        .orElseThrow(() -> new BadRequestException("Sual boş ola bilməz"));

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

    public static void validationForUpdate(ExamUpdateRequest request, User user) {
        log.info("İmtahan yeniləmə üçün təsdiqlənir");
        Integer questionCountTotal =
                request.subjectStructures().stream()
                        .map(SubjectStructureQuestionsUpdateRequest::subjectStructureUpdateRequest)
                        .map(SubjectStructureUpdateRequest::questionCount)
                        .reduce(Integer::sum)
                        .orElse(0);

        if (questionCountTotal >= user.getPack().getQuestionCountPerExam())
            throw new ReachedLimitException("Bu imtahan üçün suallar limitinə çatdınız");

        List<QuestionUpdateRequestForExam> questionRequests =
                request.subjectStructures().stream()
                        .map(SubjectStructureQuestionsUpdateRequest::questionRequests)
                        .reduce(
                                (a, b) -> {
                                    a.addAll(b);
                                    return a;
                                })
                        .orElseThrow(() -> new BadRequestException("Suallar boş ola bilməz"));

        ExamValidationService.validateExamForUpdate(
                user,
                questionRequests,
                request.hasPicture(),
                request.hasPdfPicture(),
                request.hasSound(),
                request.isHidden(),
                request.subjectStructures().size(),
                request.durationInSeconds());
    }

    private static void validateExamForUpdate(
            User user,
            List<QuestionUpdateRequestForExam> questionRequests,
            boolean hasPicture,
            boolean hasPdfPicture,
            boolean hasSound,
            boolean hidden,
            int size,
            Integer integer) {
        Optional<QuestionUpdateRequestForExam> fistManualCheckQuestion =
                questionRequests.stream()
                        .filter(
                                questionRequest ->
                                        QuestionType.OPEN_ENDED.equals(questionRequest.questionType())
                                                && (questionRequest.questionDetails().isAuto() != null
                                                && !questionRequest.questionDetails().isAuto()))
                        .findFirst();
        if (!user.getPack().isCanAddManualCheckAutoQuestion() && fistManualCheckQuestion.isPresent())
            throw new BadRequestException("Bu imtahana əl ilə yoxlama sualı əlavə edə bilməzsiniz");

        validatePack(user, hasPicture, hasPdfPicture, hasSound, hidden, size, integer);
    }

    private static void validatePack(User user, boolean hasPicture, boolean hasPdfPicture, boolean hasSound, boolean hidden, int size, Integer integer) {
        if (!user.getPack().isCanAddPicture() && hasPicture)
            throw new BadRequestException("Bu imtahan üçün şəkil əlavə edə bilməzsiniz");

        if (!user.getPack().isCanAddPdfSound() && (hasPdfPicture || hasSound))
            throw new BadRequestException("Bu imtahan üçün səs və ya pdf şəkil əlavə edə bilməzsiniz");

        if (hidden && !user.getPack().isCanShareViaCode())
            throw new BadRequestException(
                    "Bu imtahanı kod vasitəsilə paylaşa bilməzsiniz (gizli imtahan yarada bilməz)");

        if (!user.getPack().isCanAddMultipleSubjectInOneExam() && size > 1)
            throw new BadRequestException("Bir imtahana birdən çox mövzu əlavə edə bilməzsiniz");

        if (integer != null && !user.getPack().isCanSelectExamDuration())
            throw new BadRequestException("İmtahanın müddətini seçə bilməzsiniz");
    }

}
