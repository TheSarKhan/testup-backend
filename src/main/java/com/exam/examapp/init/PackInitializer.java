package com.exam.examapp.init;

import com.exam.examapp.model.Pack;
import com.exam.examapp.repository.PackRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PackInitializer {
    private final PackRepository packRepository;

    @Value("${app.default-pack-name}")
    private static String defaultPackName;

    private static Pack getProPlus() {
        return Pack.builder()
                .header("Premium")
                .packName("Pro+")
                .price(BigDecimal.valueOf(29))
                .monthlyExamCount(100)
                .questionCountPerExam(120)
                .studentPerExam(1000)
                .totalExamCount(1000)
                .canAnalysisStudentResults(true)
                .canEditExam(true)
                .canAddPicture(true)
                .canAddPdfSound(true)
                .canShareViaCode(true)
                .canDownloadExamAsPdf(true)
                .canAddMultipleSubjectInOneExam(true)
                .canUseExamTemplate(true)
                .canAddManualCheckAutoQuestion(true)
                .canSelectExamDuration(true)
                .canUseQuestionDb(true)
                .canPrepareQuestionsDb(true)
                .build();
    }

    private static Pack getPro() {
        return Pack.builder()
                .header("Most Popular")
                .packName("Pro")
                .price(BigDecimal.valueOf(19))
                .monthlyExamCount(10)
                .questionCountPerExam(80)
                .studentPerExam(200)
                .totalExamCount(200)
                .canAnalysisStudentResults(true)
                .canEditExam(true)
                .canAddPicture(true)
                .canAddPdfSound(true)
                .canShareViaCode(true)
                .canDownloadExamAsPdf(true)
                .canAddMultipleSubjectInOneExam(false)
                .canUseExamTemplate(true)
                .canAddManualCheckAutoQuestion(true)
                .canSelectExamDuration(true)
                .canUseQuestionDb(true)
                .canPrepareQuestionsDb(true)
                .build();
    }

    private static Pack getFree() {
        return Pack.builder()
                .header("Default")
                .packName(defaultPackName)
                .price(BigDecimal.ZERO)
                .monthlyExamCount(3)
                .questionCountPerExam(15)
                .studentPerExam(30)
                .totalExamCount(20)
                .canAnalysisStudentResults(true)
                .canEditExam(false)
                .canAddPicture(true)
                .canAddPdfSound(false)
                .canShareViaCode(false)
                .canDownloadExamAsPdf(true)
                .canAddMultipleSubjectInOneExam(false)
                .canUseExamTemplate(false)
                .canAddManualCheckAutoQuestion(true)
                .canSelectExamDuration(false)
                .canUseQuestionDb(false)
                .canPrepareQuestionsDb(false)
                .build();
    }

    private static Pack getAdmin() {
        return Pack.builder()
                .header("For Admin")
                .packName("Admin")
                .price(BigDecimal.ZERO)
                .monthlyExamCount(Integer.MAX_VALUE)
                .questionCountPerExam(Integer.MAX_VALUE)
                .studentPerExam(Integer.MAX_VALUE)
                .totalExamCount(Integer.MAX_VALUE)
                .canAnalysisStudentResults(true)
                .canEditExam(true)
                .canAddPicture(true)
                .canAddPdfSound(true)
                .canShareViaCode(true)
                .canDownloadExamAsPdf(true)
                .canAddMultipleSubjectInOneExam(true)
                .canUseExamTemplate(true)
                .canAddManualCheckAutoQuestion(true)
                .canSelectExamDuration(true)
                .canUseQuestionDb(true)
                .canPrepareQuestionsDb(true)
                .build();
    }

    @PostConstruct
    public void init() {
        if (packRepository.count() == 0) {
            packRepository.saveAll(
                    List.of(
                            getFree(),
                            getPro(),
                            getProPlus(),
                            getAdmin()));
            log.info("Packs initialized.");
        }
    }
}
