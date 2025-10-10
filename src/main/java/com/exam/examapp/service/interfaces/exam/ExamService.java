package com.exam.examapp.service.interfaces.exam;

import com.exam.examapp.dto.request.exam.ExamRequest;
import com.exam.examapp.dto.request.exam.ExamUpdateRequest;
import com.exam.examapp.dto.response.ResultStatisticResponse;
import com.exam.examapp.dto.response.exam.ExamBlockResponse;
import com.exam.examapp.dto.response.exam.ExamDetailedResponse;
import com.exam.examapp.dto.response.exam.ExamResponse;
import com.exam.examapp.dto.response.exam.StartExamResponse;
import com.exam.examapp.model.exam.Exam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface ExamService {
    void createExam(
            ExamRequest request,
            List<MultipartFile> titles,
            List<MultipartFile> variantPictures,
            List<MultipartFile> numberPictures,
            List<MultipartFile> sounds);

    List<ExamBlockResponse> getAllExams(String name,
                                        Integer minCost,
                                        Integer maxCost,
                                        List<Integer> rating,
                                        List<UUID> tagIds,
                                        Integer pageNum);

    List<ExamBlockResponse> getAllExamsForAdmin(String name,
                                        Integer minCost,
                                        Integer maxCost,
                                        List<Integer> rating,
                                        List<UUID> tagIds,
                                        Integer pageNum);

    List<ExamBlockResponse> getMyExams();

    List<ExamBlockResponse> getAdminCooperationExams();

    List<ExamBlockResponse> getExamByTag(List<UUID> tagIds);

    List<ExamBlockResponse> getLastCreatedExams();

    ExamDetailedResponse getExamDetailedById(UUID id);

    ExamResponse getExamById(UUID id);

    Exam getById(UUID id);

    Integer getExamCode(UUID id);

    String getExamStartLink(UUID id);

    StartExamResponse startExamViaCode(String studentName, String examCode);

    StartExamResponse startExamViaId(String studentName, UUID id);

    ResultStatisticResponse finishExam(UUID studentExamId);

    ResultStatisticResponse getResultStatistic(UUID studentExamId);

    void publishExam(UUID id);

    void updateExam(
            ExamUpdateRequest request,
            List<MultipartFile> titles,
            List<MultipartFile> variantPictures,
            List<MultipartFile> numberPictures,
            List<MultipartFile> sounds);

    void deleteExam(UUID id);
}
