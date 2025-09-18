package com.exam.examapp.mapper;

import com.exam.examapp.dto.request.PackRequest;
import com.exam.examapp.dto.request.PackUpdateRequest;
import com.exam.examapp.model.Pack;

public class PackMapper {
    public static Pack requestTo(PackRequest request) {
        return Pack.builder()
                .header(request.header())
                .packName(request.packName())
                .price(request.packPrice())
                .monthlyExamCount(request.monthlyExamCount())
                .questionCountPerExam(request.questionCountPerExam())
                .studentPerExam(request.studentPerExam())
                .totalExamCount(request.totalExamCount())
                .canAnalysisStudentResults(request.canAnalysisStudentResults())
                .canEditExam(request.canEditExam())
                .canAddPicture(request.canAddPicture())
                .canAddPdfSound(request.canAddPdfSound())
                .canShareViaCode(request.canShareViaCode())
                .canDownloadExamAsPdf(request.canDownloadExamAsPdf())
                .canAddMultipleSubjectInOneExam(request.canAddMultipleSubjectInOneExam())
                .canUseExamTemplate(request.canUseExamTemplate())
                .canAddManualCheckAutoQuestion(request.canAddManualCheckAutoQuestion())
                .canSelectExamDuration(request.canSelectExamDuration())
                .canUseQuestionDb(request.canUseQuestionDb())
                .canPrepareQuestionsDb(request.canPrepareQuestionsDb())
                .build();
    }

    public static Pack updateRequestTo(Pack oldPack, PackUpdateRequest request) {
        oldPack.setHeader(request.header());
        oldPack.setPackName(request.packName());
        oldPack.setPrice(request.packPrice());
        oldPack.setMonthlyExamCount(request.monthlyExamCount());
        oldPack.setQuestionCountPerExam(request.questionCountPerExam());
        oldPack.setStudentPerExam(request.studentPerExam());
        oldPack.setTotalExamCount(request.totalExamCount());
        oldPack.setCanAnalysisStudentResults(request.canAnalysisStudentResults());
        oldPack.setCanEditExam(request.canEditExam());
        oldPack.setCanAddPicture(request.canAddPicture());
        oldPack.setCanAddPdfSound(request.canAddPdfSound());
        oldPack.setCanShareViaCode(request.canShareViaCode());
        oldPack.setCanDownloadExamAsPdf(request.canDownloadExamAsPdf());
        oldPack.setCanAddMultipleSubjectInOneExam(request.canAddMultipleSubjectInOneExam());
        oldPack.setCanUseExamTemplate(request.canUseExamTemplate());
        oldPack.setCanAddManualCheckAutoQuestion(request.canAddManualCheckAutoQuestion());
        oldPack.setCanSelectExamDuration(request.canSelectExamDuration());
        oldPack.setCanUseQuestionDb(request.canUseQuestionDb());
        oldPack.setCanPrepareQuestionsDb(request.canPrepareQuestionsDb());
        return oldPack;
    }
}
