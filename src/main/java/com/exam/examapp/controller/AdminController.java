package com.exam.examapp.controller;

import com.exam.examapp.dto.request.StudentFilter;
import com.exam.examapp.dto.request.TeacherFilter;
import com.exam.examapp.dto.response.AdminStatisticsResponse;
import com.exam.examapp.dto.response.ApiResponse;
import com.exam.examapp.dto.response.LogResponse;
import com.exam.examapp.dto.response.UsersForAdminResponse;
import com.exam.examapp.dto.response.exam.ExamBlockResponse;
import com.exam.examapp.model.User;
import com.exam.examapp.model.enums.Role;
import com.exam.examapp.service.impl.exam.helper.ExamSort;
import com.exam.examapp.service.impl.exam.helper.ExamType;
import com.exam.examapp.service.interfaces.AdminService;
import com.exam.examapp.service.interfaces.LogService;
import com.exam.examapp.service.interfaces.UserService;
import com.exam.examapp.service.interfaces.exam.ExamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/v1/admin")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Admin Management", description = "Endpoints for managing admin accounts")
public class AdminController {
    private final AdminService adminService;

    private final ExamService examService;

    private final UserService userService;

    private final LogService logService;

    @GetMapping("/users-by-role")
    @Operation(summary = "Get Users by role", description = "Retrieve a list of users filtered by role.")
    public ResponseEntity<ApiResponse<List<User>>> getUsersByRole(@RequestParam Role role) {
        List<User> users = userService.getUsersByRole(role);
        return ResponseEntity.ok(ApiResponse.build(HttpStatus.OK, "İstifadəçilər uğurla əldə edildi", users));
    }

    @PatchMapping("/change-role/id")
    @Operation(summary = "Change role", description = "Allows an **ADMIN** to change the role of user by id.")
    public ResponseEntity<ApiResponse<Void>> changeRole(@RequestParam UUID id, @RequestParam Role role) {
        adminService.changeUserRoleViaId(id, role);
        return ResponseEntity.ok(ApiResponse.build(HttpStatus.OK, "Rol uğurla dəyişdirildi", null));
    }

    @PatchMapping("/change-role/email")
    @Operation(summary = "Change role", description = "Allows an **ADMIN** to change the role of user by email.")
    public ResponseEntity<ApiResponse<Void>> changeRoleViaEmail(@RequestParam String email, @RequestParam Role role) {
        adminService.changeUserRoleViaEmail(email, role);
        return ResponseEntity.ok(ApiResponse.build(HttpStatus.OK, "Rol uğurla dəyişdirildi", null));
    }

    @GetMapping("/statistics")
    @Operation(summary = "Get Statistics", description = "Retrieve statistics.")
    public ResponseEntity<ApiResponse<AdminStatisticsResponse>> getStatistics() {
        AdminStatisticsResponse adminStatistics = adminService.getAdminStatistics();
        return ResponseEntity.ok(ApiResponse.build(HttpStatus.OK, "Statistika uğurla əldə edildi", adminStatistics));
    }

    @GetMapping("/teachers-by-search")
    @Operation(summary = "Get Teachers by search", description = "Retrieve a list of teachers filtered by search.")
    public ResponseEntity<ApiResponse<Map<Integer, List<UsersForAdminResponse>>>> getTeachersBySearch(
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam(required = false) String search) {
        Map<Integer, List<UsersForAdminResponse>> map =
                adminService.getTeachersByNameOrEmail(page, size, search);
        return ResponseEntity.ok(ApiResponse.build(HttpStatus.OK, "Müəllimlər uğurla əldə edildi", map));
    }

    @GetMapping("/students-by-search")
    @Operation(summary = "Get Students by search", description = "Retrieve a list of students filtered by search.")
    public ResponseEntity<ApiResponse<Map<Integer, List<UsersForAdminResponse>>>> getStudentsBySearch(
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam(required = false) String search) {
        Map<Integer, List<UsersForAdminResponse>> map =
                adminService.getStudentsByNameOrEmail(page, size, search);
        return ResponseEntity.ok(ApiResponse.build(HttpStatus.OK, "Tələbələr uğurla əldə edildi", map));
    }

    @GetMapping("/teachers-by-filter")
    @Operation(summary = "Get Teachers by filter", description = "Retrieve a list of teachers filtered.")
    public ResponseEntity<ApiResponse<Map<Integer, List<UsersForAdminResponse>>>> getTeachersByFilter(
            @RequestParam(required = false) List<String> packNames,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) Instant createAtAfter,
            @RequestParam(required = false) Instant createAtBefore,
            @RequestParam int page,
            @RequestParam int size) {
        TeacherFilter filter = new TeacherFilter(packNames, isActive, createAtAfter, createAtBefore, page, size);
        Map<Integer, List<UsersForAdminResponse>> map = adminService.getTeachersByFiltered(filter);
        return ResponseEntity.ok(ApiResponse.build(HttpStatus.OK, "Müəllimlər uğurla əldə edildi", map));
    }

    @GetMapping("/students-by-filter")
    @Operation(summary = "Get Students by filter", description = "Retrieve a list of students filtered.")
    public ResponseEntity<ApiResponse<Map<Integer, List<UsersForAdminResponse>>>> getStudentsByFilter(
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) Instant createAtAfter,
            @RequestParam(required = false) Instant createAtBefore,
            @RequestParam int page,
            @RequestParam int size) {
        StudentFilter filter = new StudentFilter(isActive, createAtAfter, createAtBefore, page, size);
        Map<Integer, List<UsersForAdminResponse>> map = adminService.getStudentsByFiltered(filter);
        return ResponseEntity.ok(ApiResponse.build(HttpStatus.OK, "Tələbələr uğurla əldə edildi", map));
    }

    @GetMapping("/logs")
    @Operation(summary = "Get Logs", description = "Retrieve list of logs.")
    public ResponseEntity<ApiResponse<List<LogResponse>>> getLogs(@RequestParam int page,
                                                                  @RequestParam int size) {
        List<LogResponse> logs = logService.getAllOrderByCreatedAtDesc(page, size);
        return ResponseEntity.ok(
                ApiResponse.build(
                        HttpStatus.OK,
                        "Qeydlər uğurla əldə edildi",
                        logs));
    }

    @GetMapping("/logs-filtered")
    @Operation(summary = "Get Logs", description = "Retrieve list of logs.")
    public ResponseEntity<ApiResponse<List<LogResponse>>> getLogsFiltered(@RequestParam(required = false) Role role,
                                                                          @RequestParam(required = false) List<String> filters,
                                                                          @RequestParam int page,
                                                                          @RequestParam int size) {
        List<LogResponse> logs = logService.getAllByFilter(role, filters, page, size);
        return ResponseEntity.ok(
                ApiResponse.build(
                        HttpStatus.OK,
                        "Qeydlər uğurla əldə edildi",
                        logs));
    }

    @GetMapping("/all-exams")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Get exams",
            description =
                    "Retrieve list of exam blocks . Returns summary info used in dashboard.")
    public ResponseEntity<ApiResponse<List<ExamBlockResponse>>> getAllExams(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer minCost,
            @RequestParam(required = false) Integer maxCost,
            @RequestParam(required = false) List<Integer> rating,
            @RequestParam(required = false) List<UUID> tagIds,
            @RequestParam(required = false) Integer pageNum,
            @RequestParam ExamSort sort,
            @RequestParam ExamType type
    ) {
        List<ExamBlockResponse> myExams = examService.getAllExamsForAdmin(name, minCost, maxCost, rating, tagIds, sort, type, pageNum);
        return ResponseEntity.ok(
                ApiResponse.build(HttpStatus.OK, "İmtahanlar uğurla əldə edildi", myExams));
    }

    @GetMapping("/exam-by-teacher")
    @Operation(summary = "Get Exam by teacher", description = "Retrieve a list of exams filtered by teacher.")
    public ResponseEntity<ApiResponse<List<ExamBlockResponse>>> getExamByTeacher(
            @RequestParam UUID teacherId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer minCost,
            @RequestParam(required = false) Integer maxCost,
            @RequestParam(required = false) List<Integer> rating,
            @RequestParam(required = false) List<UUID> tagIds,
            @RequestParam(required = false) Integer pageNum,
            @RequestParam ExamSort sort,
            @RequestParam ExamType type) {
        List<ExamBlockResponse> examsByTeacher =
                adminService.getExamsByTeacher(teacherId, name, minCost, maxCost, rating, tagIds, sort, type, pageNum);
        return ResponseEntity.ok(
                ApiResponse.build(HttpStatus.OK,
                        "İmtahanlar uğurla əldə edildi", examsByTeacher));
    }

    @GetMapping("/teacher-cooperation-exams")
    @Operation(summary = "Get Teacher cooperation exams", description = "Retrieve a list of teacher cooperation exams.")
    public ResponseEntity<ApiResponse<List<ExamBlockResponse>>> getTeacherCooperationExams() {
        List<ExamBlockResponse> cooperationExams = adminService.getTeacherCooperationExams();
        return ResponseEntity.ok(
                ApiResponse.
                        build(HttpStatus.OK,
                                "Birlesmis imtahanlar elde edildi",
                                cooperationExams));
    }

    @GetMapping("/simple-exam-by-teacher")
    @Operation(summary = "Get Exam by teacher", description = "Retrieve a list of exams by teacher.")
    public ResponseEntity<ApiResponse<List<ExamBlockResponse>>> getExamByTeacher(
            @RequestParam UUID teacherId) {
        List<ExamBlockResponse> examsByTeacher =
                adminService.getSimpleExamsByTeacher(teacherId);
        return ResponseEntity.ok(
                ApiResponse.build(HttpStatus.OK,
                        "İmtahanlar uğurla əldə edildi", examsByTeacher));
    }

    @GetMapping("/simple-exam-by-student")
    @Operation(summary = "Get Exam by student", description = "Retrieve a list of exams by student.")
    public ResponseEntity<ApiResponse<List<ExamBlockResponse>>> getExamByStudent(
            @RequestParam UUID student) {
        List<ExamBlockResponse> examsByTeacher =
                adminService.getSimpleExamsByStudent(student);
        return ResponseEntity.ok(
                ApiResponse.build(HttpStatus.OK,
                        "İmtahanlar uğurla əldə edildi", examsByTeacher));
    }

    @PatchMapping("/change-pack")
    @Operation(summary = "Change pack", description = "Allows an **ADMIN** to change the pack of teacher by id.")
    public ResponseEntity<ApiResponse<Void>> changePack(@RequestParam UUID id, @RequestParam UUID packId) {
        adminService.changeTeacherPack(id, packId);
        return ResponseEntity.ok(ApiResponse.build(HttpStatus.OK, "Paket uğurla dəyişdirildi", null));
    }

    @PatchMapping("/deactivate-user")
    @Operation(summary = "Deactivate user", description = "Allows an **ADMIN** to deactivate a user by id.")
    public ResponseEntity<ApiResponse<Void>> deactivateUser(@RequestParam UUID id) {
        adminService.deactivateUser(id);
        return ResponseEntity.ok(ApiResponse.build(HttpStatus.OK, "Istifadəçi deaktiv edildi", null));
    }

    @PatchMapping("/activate-user")
    @Operation(summary = "Activate user", description = "Allows an **ADMIN** to activate a user by id.")
    public ResponseEntity<ApiResponse<Void>> activateUser(@RequestParam UUID id) {
        adminService.activateUser(id);
        return ResponseEntity.ok(ApiResponse.build(HttpStatus.OK, "Istifadəçi aktiv edildi", null));
    }

    @GetMapping("/filtered-emails")
    @Operation(summary = "Get filtered emails", description = "Retrieve a list of filtered emails.")
    public ResponseEntity<ApiResponse<List<String>>> getFilteredEmails(
            @RequestParam(required = false) List<String> packNames,
            @RequestParam(required = false) List<Role> roles,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) Instant createAtAfter,
            @RequestParam(required = false) Instant createAtBefore) {
        List<String> emailList = userService.getEmailList(packNames, roles, isActive, createAtAfter, createAtBefore);
        return ResponseEntity.ok(ApiResponse.build(HttpStatus.OK, "E-poçtlar uğurla filtrləndi", emailList));
    }
}
