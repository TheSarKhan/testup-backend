package com.exam.examapp.service.interfaces.exam;

import com.exam.examapp.dto.request.exam.ExamRequest;
import com.exam.examapp.dto.request.exam.ExamUpdateRequest;
import com.exam.examapp.dto.response.ResultStatisticResponse;
import com.exam.examapp.dto.response.exam.*;
import com.exam.examapp.model.User;
import com.exam.examapp.model.exam.Exam;
import com.exam.examapp.service.impl.exam.helper.ExamSort;
import com.exam.examapp.service.impl.exam.helper.ExamType;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;

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
                                        ExamSort sort,
                                        ExamType type,
                                        Integer pageNum);

    List<ExamBlockResponse> getAllExamsForAdmin(String name,
                                                Integer minCost,
                                                Integer maxCost,
                                                List<Integer> rating,
                                                List<UUID> tagIds,
                                                ExamSort sort,
                                                ExamType type,
                                                Integer pageNum);

    List<ExamBlockResponse> getMyExams();

    List<ExamBlockResponse> getExamsByUserId(UUID id);

    List<ExamBlockResponse> getAdminCooperationExams();

    List<ExamBlockResponse> getExamByTag(List<UUID> tagIds);

    List<ExamBlockResponse> getLastCreatedExams();

    ExamDetailedResponse getExamDetailedById(UUID id);

    ExamStartLinkResponse getExamStartInformationById(UUID id);

    ExamResponse getExamById(UUID id);

    Exam getById(UUID id);

    Integer getExamCode(UUID id);

    String getExamLink(UUID id);

    StartExamResponse startExamViaCode(String studentName, String examCode);

    StartExamResponse startExamViaId(String studentName, UUID id);

    ResultStatisticResponse finishExam(UUID studentExamId);

    ResultStatisticResponse getResultStatistic(UUID studentExamId);

    void publishExam(UUID id);

    void unpublishExam(UUID id);

    void updateExam(
            ExamUpdateRequest request,
            List<MultipartFile> titles,
            List<MultipartFile> variantPictures,
            List<MultipartFile> numberPictures,
            List<MultipartFile> sounds);

    void deleteExam(UUID id);

    List<Exam> getExamsFiltered(UUID teacherId,
                                String name,
                                Integer minCost,
                                Integer maxCost,
                                List<Integer> rating,
                                List<UUID> tagIds,
                                Integer pageNum,
                                ExamSort sort,
                                ExamType type);

    Function<Exam, ExamBlockResponse> examToResponse(User user);

    void giveRatingToExam(UUID examId, Integer rating);
}
