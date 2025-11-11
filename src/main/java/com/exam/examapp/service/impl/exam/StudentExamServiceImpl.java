package com.exam.examapp.service.impl.exam;

import com.exam.examapp.dto.request.NotificationRequest;
import com.exam.examapp.exception.custom.BadRequestException;
import com.exam.examapp.exception.custom.ResourceNotFoundException;
import com.exam.examapp.model.User;
import com.exam.examapp.model.enums.ExamStatus;
import com.exam.examapp.model.exam.Exam;
import com.exam.examapp.model.exam.StudentExam;
import com.exam.examapp.repository.StudentExamRepository;
import com.exam.examapp.security.service.interfaces.EmailService;
import com.exam.examapp.service.interfaces.FileService;
import com.exam.examapp.service.interfaces.LogService;
import com.exam.examapp.service.interfaces.NotificationService;
import com.exam.examapp.service.interfaces.UserService;
import com.exam.examapp.service.interfaces.exam.ExamService;
import com.exam.examapp.service.interfaces.exam.StudentExamService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudentExamServiceImpl implements StudentExamService {
    private static final String IMAGE_PATH = "uploads/images/student_exams";

    private final StudentExamRepository studentExamRepository;

    private final UserService userService;

    private final ExamService examService;

    private final FileService fileService;

    private final LogService logService;

    private final NotificationService notificationService;

    private final EmailService emailService;

    @Override
    @Transactional
    public void addExam(UUID studentId, UUID examId) {
        log.info("İmtahan sagirdə əlavə olunur");
        Exam exam = examService.getById(examId);
        User student = userService.getUserById(studentId);
        List<StudentExam> byExamAndStudent =
                studentExamRepository.getByExamAndStudent(exam, student).stream()
                        .filter(e -> ExamStatus.ACTIVE.equals(e.getStatus()))
                        .toList();

        if (!byExamAndStudent.isEmpty())
            throw new BadRequestException("Bu imtahan Sagirdə mövcuddur");

        studentExamRepository.save(
                StudentExam.builder()
                        .numberOfQuestions(
                                exam.getSubjectStructureQuestions().stream()
                                        .map(
                                                subjectStructureQuestion ->
                                                        subjectStructureQuestion.getSubjectStructure().getQuestionCount())
                                        .mapToInt(Integer::intValue)
                                        .sum())
                        .student(student)
                        .status(ExamStatus.ACTIVE)
                        .exam(exam)
                        .build());

        notificationService.sendNotification(
                new NotificationRequest(
                        "testup.az imtahanın əlavəsi",
                        "Hesabınıza imtahan hesabınıza əlavə edildi. İmtahan adı: " + exam.getExamTitle(),
                        student.getEmail()
                )
        );

        emailService.sendEmail(
                student.getEmail(),
                "testup.az imtahanın əlavəsi",
                "Hesabınıza imtahan hesabınıza əlavə edildi. İmtahan adı: " + exam.getExamTitle()
        );

        String message = "İmtahan sagirdə əlavə olundu. Email: " + student.getEmail() + " imtahan adı: " + exam.getExamTitle();

        log.info(message);
        logService.save(message, userService.getCurrentUserOrNull());
    }

    @Override
    public void listeningPlayed(UUID studentExamId, UUID ListeningId) {
        User user = userService.getCurrentUserOrNull();

        StudentExam studentExam = getById(studentExamId);

        if (user != null && !studentExam.getStudent().getId().equals(user.getId()))
            throw new BadRequestException("Digər tələbə imtahanını yeniləyə bilməzsiniz");

        Map<UUID, Integer> listeningIdToPlayTimeMap = studentExam.getListeningIdToPlayTimeMap();

        if (listeningIdToPlayTimeMap == null) listeningIdToPlayTimeMap = new HashMap<>();

        listeningIdToPlayTimeMap.put(
                ListeningId, listeningIdToPlayTimeMap.getOrDefault(ListeningId, 0) + 1);

        studentExam.setListeningIdToPlayTimeMap(listeningIdToPlayTimeMap);
        studentExamRepository.save(studentExam);
        log.info("Dinləmə uğurla keçdi");
    }

    private StudentExam getById(UUID studentExamId) {
        return studentExamRepository
                .findById(studentExamId)
                .orElseThrow(() -> new ResourceNotFoundException("Tələbə İmtahanı tapılmadı"));
    }

    @Override
    public void saveAnswer(UUID studentExamId, UUID questionId, String answer, MultipartFile file) {
        log.info("Cavab yadda saxlanılır");

        if ((answer == null || answer.isEmpty()) && file == null) {
            log.info("Cavab bosdur save olunmadi.");
            return;
        }

        StudentExam studentExam = getById(studentExamId);
        Map<UUID, String> questionIdToAnswerMap =
                studentExam.getQuestionIdToAnswerMap() == null
                        ? new HashMap<>()
                        : studentExam.getQuestionIdToAnswerMap();
        if (file != null) {
            answer = fileService.uploadFile(IMAGE_PATH, file);
        }

        questionIdToAnswerMap.put(questionId, answer);
        studentExam.setQuestionIdToAnswerMap(questionIdToAnswerMap);

        Map<UUID, Boolean> questionIdToIsAnswerPictureMap = studentExam.getQuestionIdToIsAnswerPictureMap();
        questionIdToIsAnswerPictureMap.put(questionId, true);
        studentExam.setQuestionIdToIsAnswerPictureMap(questionIdToIsAnswerPictureMap);

        studentExamRepository.save(studentExam);
        log.info("Cavab uğurla yadda saxlanıldı");
    }

    @Override
    @Transactional
    public StudentExam getStudentExam(UUID studentExamId) {
        StudentExam studentExam = studentExamRepository.findById(studentExamId).orElseThrow(
                () -> new ResourceNotFoundException("Student exam tapilmadi."));
        log.info("Student exam getirildi:{}", studentExam.toString());
        return studentExam;
    }
}
