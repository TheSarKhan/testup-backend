package com.exam.examapp.service.interfaces;

import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface StudentExamService {
    void addExam(UUID studentId, UUID examId);

    void listeningPlayed(UUID studentExamId, UUID ListeningId);

    void saveAnswer(UUID studentExamId, UUID questionId, String answer, MultipartFile file);
}
