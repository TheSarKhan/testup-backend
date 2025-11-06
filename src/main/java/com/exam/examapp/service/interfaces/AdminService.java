package com.exam.examapp.service.interfaces;

import com.exam.examapp.dto.request.StudentFilter;
import com.exam.examapp.dto.request.TeacherFilter;
import com.exam.examapp.dto.response.AdminStatisticsResponse;
import com.exam.examapp.dto.response.UsersForAdminResponse;
import com.exam.examapp.dto.response.exam.ExamBlockResponse;
import com.exam.examapp.model.enums.Role;
import com.exam.examapp.service.impl.exam.helper.ExamSort;
import com.exam.examapp.service.impl.exam.helper.ExamType;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface AdminService {
    void changeUserRoleViaEmail(String email, Role role);

    void changeUserRoleViaId(UUID id, Role role);

    AdminStatisticsResponse getAdminStatistics();

    Map<Integer, List<UsersForAdminResponse>> getTeachersByNameOrEmail(int page, int size, String search);

    Map<Integer, List<UsersForAdminResponse>> getStudentsByNameOrEmail(int page, int size, String search);

    Map<Integer, List<UsersForAdminResponse>> getTeachersByFiltered(TeacherFilter filter);

    Map<Integer, List<UsersForAdminResponse>> getStudentsByFiltered(StudentFilter filter);

    List<ExamBlockResponse> getExamsByTeacher(UUID id,
                                              String name,
                                              Integer minCost,
                                              Integer maxCost,
                                              List<Integer> rating,
                                              List<UUID> tagIds,
                                              ExamSort sort,
                                              ExamType type,
                                              Integer pageNum);

    List<ExamBlockResponse> getSimpleExamsByTeacher(UUID id);

    void deactivateUser(UUID id);

    void activateUser(UUID id);

    void changeTeacherPack(UUID id, UUID packId);
}
