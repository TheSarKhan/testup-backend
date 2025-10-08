package com.exam.examapp.controller;

import com.exam.examapp.dto.response.AdminStatisticsResponse;
import com.exam.examapp.dto.response.ApiResponse;
import com.exam.examapp.model.User;
import com.exam.examapp.model.enums.Role;
import com.exam.examapp.service.interfaces.AdminService;
import com.exam.examapp.service.interfaces.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/v1/admin")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Admin Management", description = "Endpoints for managing admin accounts")
public class AdminController {
    private final AdminService adminService;

    private final UserService userService;

    @GetMapping("/users-by-role")
    @Operation(summary = "Get Users by role", description = "Retrieve a list of users filtered by role.")
    public ResponseEntity<ApiResponse<List<User>>> getUsersByRole(@RequestParam Role role) {
        List<User> users = userService.getUsersByRole(role);
        return ResponseEntity.ok(ApiResponse.build(HttpStatus.OK, "Users retrieved successfully", users));
    }

    @PatchMapping("/change-role/id")
    @Operation(summary = "Change role", description = "Allows an **ADMIN** to change the role of user by id.")
    public ResponseEntity<ApiResponse<Void>> changeRole(@RequestParam UUID id, @RequestParam Role role) {
        adminService.changeUserRoleViaId(id, role);
        return ResponseEntity.ok(ApiResponse.build(HttpStatus.OK, "Role changed successfully", null));
    }

    @PatchMapping("/change-role/email")
    @Operation(summary = "Change role", description = "Allows an **ADMIN** to change the role of user by email.")
    public ResponseEntity<ApiResponse<Void>> changeRoleViaEmail(@RequestParam String email, @RequestParam Role role) {
        adminService.changeUserRoleViaEmail(email, role);
        return ResponseEntity.ok(ApiResponse.build(HttpStatus.OK, "Role changed successfully", null));
    }

    @GetMapping("/statistics")
    public ResponseEntity<ApiResponse<AdminStatisticsResponse>> getStatistics() {
        AdminStatisticsResponse adminStatistics = adminService.getAdminStatistics();
        return ResponseEntity.ok(ApiResponse.build(HttpStatus.OK, "Statistics retrieved successfully", adminStatistics));
    }
}
