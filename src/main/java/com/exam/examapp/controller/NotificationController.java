package com.exam.examapp.controller;

import com.exam.examapp.dto.request.MultiNotificationRequest;
import com.exam.examapp.dto.request.NotificationRequest;
import com.exam.examapp.dto.request.NotificationUpdateRequest;
import com.exam.examapp.dto.response.ApiResponse;
import com.exam.examapp.dto.response.NotificationResponse;
import com.exam.examapp.service.interfaces.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notification")
@Tag(name = "Notification Management", description = "Endpoints for managing and retrieving notifications")
public class NotificationController {
    private final NotificationService notificationService;

    @PostMapping
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Send a notification",
            description = "Allows an **ADMIN** to send a new notification by providing necessary details."
    )
    public ResponseEntity<ApiResponse<Void>> send(@RequestBody NotificationRequest request) {
        notificationService.sendNotification(request);
        return ResponseEntity.ok(
                ApiResponse.build(
                        HttpStatus.CREATED,
                        "Bildiriş uğurla göndərildi",
                        null));
    }

    @PostMapping("/multi")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Send notifications",
            description = "Allows an **ADMIN** to send new notifications by providing necessary details."
    )
    public ResponseEntity<ApiResponse<Void>> sendNotifications(@RequestBody MultiNotificationRequest request) {
        notificationService.sendNotificationToAll(request);
        return ResponseEntity.ok(
                ApiResponse.build(
                        HttpStatus.CREATED,
                        "Bildirişlər uğurla göndərildi",
                        null));
    }

    @GetMapping
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Retrieve all notifications",
            description = "Fetches a list of all notifications. Only accessible by **ADMIN**."
    )
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getAll(@RequestParam int pageNum,
                                                                          @RequestParam int pageSize) {
        List<NotificationResponse> allNotifications =
                notificationService.getAllNotifications(pageSize, pageNum);
        return ResponseEntity.ok(
                ApiResponse.build(
                        HttpStatus.OK,
                        "Bildirişlər uğurla əldə edildi",
                        allNotifications));
    }

    @GetMapping("/my")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Get my notifications",
            description = "Fetches all notifications for the currently authenticated user."
    )
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getMy() {
        List<NotificationResponse> myNotifications = notificationService.getMyNotifications();
        return ResponseEntity.ok(
                ApiResponse.build(
                        HttpStatus.OK,
                        "Bildirişlərim uğurla əldə edildi",
                        myNotifications));
    }

    @GetMapping("/email")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Get notifications by email",
            description = "Allows an **ADMIN** to fetch all notifications for a specific email address."
    )
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getByEmail(@RequestParam
                                                                              @Email
                                                                              String email) {
        List<NotificationResponse> notificationsByEmail = notificationService.getNotificationsByEmail(email);
        return ResponseEntity.ok(
                ApiResponse.build(
                        HttpStatus.OK,
                        "Bildirişlər uğurla əldə edildi",
                        notificationsByEmail));
    }

    @GetMapping("/id")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Get notification by ID",
            description = "Retrieve a specific notification by its unique ID."
    )
    public ResponseEntity<ApiResponse<NotificationResponse>> getById(@RequestParam
                                                                     @NotNull
                                                                     UUID id) {
        NotificationResponse notification = notificationService.getNotificationById(id);
        return ResponseEntity.ok(
                ApiResponse.build(
                        HttpStatus.OK,
                        "Bildiriş uğurla alındı",
                        notification));
    }

    @PutMapping
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Update a notification",
            description = "Allows an **ADMIN** to update an existing notification by providing its ID and updated details."
    )
    public ResponseEntity<ApiResponse<Void>> update(@RequestBody NotificationUpdateRequest request) {
        notificationService.updateNotification(request);
        return ResponseEntity.ok(
                ApiResponse.build(
                        HttpStatus.OK,
                        "Bildiriş uğurla yeniləndi",
                        null));
    }

    @DeleteMapping
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Delete a notification",
            description = "Allows an **ADMIN** to delete a notification by its unique ID."
    )
    public ResponseEntity<ApiResponse<Void>> delete(@RequestParam UUID id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.ok(
                ApiResponse.build(
                        HttpStatus.NO_CONTENT,
                        "Bildiriş uğurla silindi",
                        null));
    }
}
