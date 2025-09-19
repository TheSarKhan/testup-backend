package com.exam.examapp.service.impl.exam;

import com.exam.examapp.exception.custom.BadRequestException;
import com.exam.examapp.model.User;
import com.exam.examapp.model.enums.ExamStatus;
import com.exam.examapp.model.exam.Exam;
import com.exam.examapp.model.exam.StudentExam;
import com.exam.examapp.repository.StudentExamRepository;
import com.exam.examapp.service.interfaces.exam.ExamService;
import com.exam.examapp.service.interfaces.FileService;
import com.exam.examapp.service.interfaces.exam.StudentExamService;
import com.exam.examapp.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StudentExamServiceImpl implements StudentExamService {
    private static final String IMAGE_PATH = "uploads/images/student_exams";

    private final StudentExamRepository studentExamRepository;

    private final UserService userService;

    private final ExamService examService;

    private final FileService fileService;

    @Override
    public void addExam(UUID studentId, UUID examId) {
        Exam exam = examService.getById(examId);
        List<StudentExam> byExamAndStudent =
                studentExamRepository.getByExamAndStudent(exam, userService.getUserById(studentId)).stream()
                        .filter(e -> ExamStatus.ACTIVE.equals(e.getStatus()))
                        .toList();

        if (!byExamAndStudent.isEmpty())
            throw new BadRequestException("You have already taken this exam.");

        studentExamRepository.save(
                StudentExam.builder()
                        .numberOfQuestions(
                                exam.getSubjectStructureQuestions().stream()
                                        .map(
                                                subjectStructureQuestion ->
                                                        subjectStructureQuestion.getSubjectStructure().getQuestionCount())
                                        .mapToInt(Integer::intValue)
                                        .sum())
                        .student(userService.getUserById(studentId))
                        .status(ExamStatus.ACTIVE)
                        .exam(exam)
                        .build());
    }

    @Override
    public void listeningPlayed(UUID studentExamId, UUID ListeningId) {
        User user = userService.getCurrentUserOrNull();

        StudentExam studentExam = getById(studentExamId);

        if (user != null && !studentExam.getStudent().getId().equals(user.getId()))
            throw new BadRequestException("You cannot update other student exam.");

        Map<UUID, Integer> listeningIdToPlayTimeMap = studentExam.getListeningIdToPlayTimeMap();

        if (listeningIdToPlayTimeMap == null) listeningIdToPlayTimeMap = new HashMap<>();

        listeningIdToPlayTimeMap.put(
                ListeningId, listeningIdToPlayTimeMap.getOrDefault(ListeningId, 0) + 1);

        studentExam.setListeningIdToPlayTimeMap(listeningIdToPlayTimeMap);
        studentExamRepository.save(studentExam);
    }

    private StudentExam getById(UUID studentExamId) {
        return studentExamRepository
                .findById(studentExamId)
                .orElseThrow(() -> new IllegalArgumentException("Student Exam not found"));
    }

    @Override
    public void saveAnswer(UUID studentExamId, UUID questionId, String answer, MultipartFile file) {
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
    }
}
