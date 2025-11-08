package com.exam.examapp.service.impl.exam.helper;

import com.exam.examapp.dto.QuestionDetails;
import com.exam.examapp.dto.request.NotificationRequest;
import com.exam.examapp.model.enums.AnswerStatus;
import com.exam.examapp.model.enums.ExamStatus;
import com.exam.examapp.model.enums.QuestionType;
import com.exam.examapp.model.exam.StudentExam;
import com.exam.examapp.model.question.Question;
import com.exam.examapp.model.subject.SubjectStructureQuestion;
import com.exam.examapp.repository.ExamRepository;
import com.exam.examapp.security.service.interfaces.EmailService;
import com.exam.examapp.service.impl.exam.checker.AnswerChecker;
import com.exam.examapp.service.impl.exam.checker.AnswerCheckerFactory;
import com.exam.examapp.service.interfaces.NotificationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnswerService {
    private final AnswerCheckerFactory factory;

    private final ExamRepository examRepository;

    private final EmailService emailService;

    private final NotificationService notificationService;

    @Transactional
    public Map<UUID, AnswerStatus> checkAnswers(StudentExam studentExam,
                                                List<Integer> correctAndWrongCounts) {
        log.info("Cavablar yoxlanılır");
        if (studentExam.getQuestionIdToAnswerMap() == null) {
            return new HashMap<>();
        }
        Map<UUID, String> questionIdToAnswerMap = studentExam.getQuestionIdToAnswerMap();
        List<SubjectStructureQuestion> subjectStructureQuestions = studentExam.getExam().getSubjectStructureQuestions();

        Map<UUID, AnswerStatus> answerStatusMap = new HashMap<>();

        for (SubjectStructureQuestion subjectStructureQuestion : subjectStructureQuestions) {
            for (Question question : subjectStructureQuestion.getQuestion()) {
                if (questionIdToAnswerMap.containsKey(question.getId())) {
                    if (QuestionType.TEXT_BASED.equals(question.getType()) || QuestionType.LISTENING.equals(question.getType())) {
                        checkListeningOrText(question,
                                questionIdToAnswerMap.get(question.getId()),
                                answerStatusMap,
                                correctAndWrongCounts);
                    }
                    AnswerChecker checker = factory.getChecker(question.getType());
                    checker.check(
                            question,
                            question.getQuestionDetails(),
                            questionIdToAnswerMap.get(question.getId()),
                            answerStatusMap,
                            correctAndWrongCounts
                    );
                } else {
                    answerStatusMap.put(question.getId(), AnswerStatus.NOT_ANSWERED);
                }
            }
        }
        log.info("Cavablar yoxlanıldı");
        return answerStatusMap;
    }

    public void handleUncheckedQuestions(StudentExam studentExam, Map<UUID, AnswerStatus> answerStatusMap) {
        log.info("Yoxlanılmamış suallara baxılır");
        long count = answerStatusMap.values().stream()
                .filter(AnswerStatus.WAITING_FOR_REVIEW::equals)
                .count();

        if (count > 0) {
            log.info("Yoxlanılmamış {} sual var", count);
            studentExam.getExam().getHasUncheckedQuestionStudentExamId().add(studentExam.getId());
            studentExam.setStatus(ExamStatus.WAITING_OPEN_ENDED_QUESTION);
            studentExam.setNumberOfNotCheckedYetQuestions((int) count);
            examRepository.save(studentExam.getExam());

            String email = studentExam.getExam().getTeacher().getEmail();

            notificationService.sendNotification(new NotificationRequest(
                    "TestUp-da yoxlanılmamış sual var.",
                    "Testinizdə işarələnməmiş sualınız var. Zəhmət olmasa nəzərdən keçirin. İmtahan adı: "
                            + studentExam.getExam().getExamTitle(),
                    email));

            emailService.sendEmail(email,
                    "TestUp-da yoxlanılmamış sual var.",
                    "Testinizdə işarələnməmiş sualınız var. Zəhmət olmasa nəzərdən keçirin. İmtahan adı: "
                            + studentExam.getExam().getExamTitle());
        }
    }

    public <V> Map<String, Map<Integer, V>> mapStudentDataToSubjects(
            StudentExam studentExam, Map<UUID, V> questionIdToValueMap) {
        log.info("Tələbənin məlumatları mövzuya çevrilir");
        Map<String, Map<Integer, V>> subjectToQuestionToValue = new HashMap<>();
        questionIdToValueMap = questionIdToValueMap == null ? new HashMap<>() : questionIdToValueMap;
        for (Map.Entry<UUID, V> entry : questionIdToValueMap.entrySet()) {
            UUID key = entry.getKey();
            V value = entry.getValue();

            List<SubjectStructureQuestion> subjectQuestions = studentExam.getExam().getSubjectStructureQuestions();
            for (SubjectStructureQuestion subjectStructureQuestion : subjectQuestions) {
                String subject = subjectStructureQuestion.getSubjectStructure().getSubject().getName();
                List<Question> questions = subjectStructureQuestion.getQuestion();

                Map<Integer, V> questionToValue = subjectToQuestionToValue
                        .computeIfAbsent(subject, k -> new HashMap<>());

                for (int i = 1; i <= questions.size(); i++) {
                    UUID questionId = questions.get(i - 1).getId();
                    if (questionId.equals(key)) {
                        questionToValue.put(i, value);
                    }
                }
            }
        }
        log.info("Tələbənin məlumatları mövzuya çevrildi");
        return subjectToQuestionToValue;
    }

    public void checkListeningOrText(Question question, String answer, Map<UUID, AnswerStatus> answerStatusMap, List<Integer> counts) {
        if (question.getQuestions() == null) {
            return;
        }
        for (Question questionQuestion : question.getQuestions()) {
            QuestionDetails questionDetails = questionQuestion.getQuestionDetails();
            AnswerChecker answerChecker = factory.getChecker(questionQuestion.getType());
            answerChecker.check(questionQuestion, questionDetails, answer, answerStatusMap, counts);
        }
    }
}
