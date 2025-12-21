package com.exam.examapp.controller;

import com.exam.examapp.dto.response.ApiResponse;
import com.exam.examapp.dto.response.StudentQuestionResponse;
import com.exam.examapp.service.impl.question.StudentQuestionImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/student-question")
public class StudentQuestionController {
    private final StudentQuestionImpl studentQuestionImpl;

    @GetMapping
    public ResponseEntity<ApiResponse<StudentQuestionResponse>> getStudentQuestion(
            @RequestParam UUID studentExamId,
            @RequestParam UUID questionId) {
        StudentQuestionResponse studentQuestionResponse = studentQuestionImpl.getStudentQuestionResponse(studentExamId, questionId);
        return ResponseEntity.ok(
                ApiResponse.build(
                        HttpStatus.OK,
                        "Sual Ugurla tapildi.",
                        studentQuestionResponse));
    }
}
