package com.exam.examapp.controller;

import com.exam.examapp.dto.request.exam.ExamRequest;
import com.exam.examapp.dto.request.exam.ExamUpdateRequest;
import com.exam.examapp.dto.response.ApiResponse;
import com.exam.examapp.dto.response.ResultStatisticResponse;
import com.exam.examapp.dto.response.exam.*;
import com.exam.examapp.service.impl.exam.helper.ExamSort;
import com.exam.examapp.service.impl.exam.helper.ExamType;
import com.exam.examapp.service.interfaces.exam.ExamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/exam")
@Tag(name = "Exams",
        description = "Exam management endpoints — create, read, update, delete and start exams. "
                + "Endpoints that modify data require bearer token and roles (ADMIN, TEACHER) where noted.")
public class ExamController {
    private final ExamService examService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    @Operation(
            summary = "Create Exam",
            description =
                    "Create a new exam. Accepts an ExamRequest (structured JSON) as a multipart part and optional media lists (titles, variantPictures, numberPictures, sounds). "
                            + "Files are optional — pass only those that exist. ExamRequest contains subject structures, timing, cost and flags (hasSound/hasPicture/etc).")
    public ResponseEntity<ApiResponse<Void>> createExam(
            @RequestPart @Valid ExamRequest request,
            @RequestPart(required = false) List<MultipartFile> titles,
            @RequestPart(required = false) List<MultipartFile> variantPictures,
            @RequestPart(required = false) List<MultipartFile> numberPictures,
            @RequestPart(required = false) List<MultipartFile> sounds) {
        log.info(request.subjectStructures().toString());
        examService.createExam(request, titles, variantPictures, numberPictures, sounds);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.build(HttpStatus.CREATED, "İmtahan uğurla yaradıldı", null));
    }

    @GetMapping
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Get my exams",
            description =
                    "Retrieve list of exam blocks . Returns summary info used in dashboard.")
    public ResponseEntity<ApiResponse<List<ExamBlockResponse>>> getMyExams() {
        List<ExamBlockResponse> myExams = examService.getMyExams();
        return ResponseEntity.ok(
                ApiResponse.build(HttpStatus.OK, "İmtahanlarım uğurla əldə edildi", myExams));
    }

    @GetMapping("/user")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Get user exams",
            description =
                    "Retrieve list of exam blocks . Returns summary info used in dashboard.")
    public ResponseEntity<ApiResponse<List<ExamBlockResponse>>> getUserExams(@RequestParam UUID id) {
        List<ExamBlockResponse> exams = examService.getExamsByUserId(id);
        return ResponseEntity.ok(
                ApiResponse.build(HttpStatus.OK, "İmtahanlar uğurla əldə edildi", exams));
    }

    @GetMapping("/all")
    @Operation(
            summary = "Get exams",
            description = "Retrieve list of exam blocks")
    public ResponseEntity<ApiResponse<ExamAllResponses>> getAllExams(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer minCost,
            @RequestParam(required = false) Integer maxCost,
            @RequestParam(required = false) List<Integer> rating,
            @RequestParam(required = false) List<UUID> tagIds,
            @RequestParam int pageNum,
            @RequestParam int pageSize,
            @RequestParam ExamSort sort,
            @RequestParam ExamType type
    ) {
        ExamAllResponses allExams = examService.getAllExams(name, minCost, maxCost, rating, tagIds, sort, type, pageNum, pageSize);
        return ResponseEntity.ok(
                ApiResponse.build(HttpStatus.OK, "İmtahanlar uğurla əldə edildi", allExams));
    }

    @GetMapping("/tags")
    public ResponseEntity<ApiResponse<List<ExamBlockResponse>>> getTags(@RequestParam List<UUID> tags) {
        List<ExamBlockResponse> examByTag = examService.getExamByTag(tags);
        return ResponseEntity.ok(
                ApiResponse.build(HttpStatus.OK, "İmtahanlar uğurla əldə edildi", examByTag));
    }

    @GetMapping("/last-created")
    public ResponseEntity<ApiResponse<List<ExamBlockResponse>>> getLastCreatedExam() {
        List<ExamBlockResponse> lastCreatedExams = examService.getLastCreatedExams();
        return ResponseEntity.ok(ApiResponse.build(HttpStatus.OK, "İmtahanlar uğurla əldə edildi", lastCreatedExams));
    }

    @GetMapping("/via-admin")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    @Operation(
            summary = "Retrieve exams for Admins and Teachers",
            description = "This endpoint allows only users with ADMIN or TEACHER roles to access the cooperation exams list.")
    public ResponseEntity<ApiResponse<List<ExamBlockResponse>>> getViaAdmin() {
        List<ExamBlockResponse> adminCooperationExams = examService.getAdminCooperationExams();
        return ResponseEntity.ok(
                ApiResponse.build(HttpStatus.OK, "Admin İmtahanları uğurla əldə edildi", adminCooperationExams));
    }

    @GetMapping("/detailed/id")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Get Exam detailed by id",
            description = "Retrieve exam details by exam UUID.")
    public ResponseEntity<ApiResponse<ExamDetailedResponse>> getDetailedExamById(@RequestParam UUID id) {
        ExamDetailedResponse exam = examService.getExamDetailedById(id);
        return ResponseEntity.ok(ApiResponse.build(HttpStatus.OK, "İmtahan uğurla alındı", exam));
    }

    @GetMapping("/start-info")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Get Exam info by id",
            description = "Retrieve exam info by exam start UUID.")
    public ResponseEntity<ApiResponse<ExamStartLinkResponse>> getStartInfoById(@RequestParam UUID id) {
        ExamStartLinkResponse exam = examService.getExamStartInformationById(id);
        return ResponseEntity.ok(ApiResponse.build(HttpStatus.OK, "İmtahan info uğurla alındı", exam));
    }

    @GetMapping("/id")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    @Operation(
            summary = "Get Exam by id",
            description = "Retrieve full exam details by exam UUID.")
    public ResponseEntity<ApiResponse<ExamResponse>> getExamById(@RequestParam UUID id) {
        ExamResponse exam = examService.getExamById(id);
        return ResponseEntity.ok(ApiResponse.build(HttpStatus.OK, "İmtahan uğurla alındı", exam));
    }

    @GetMapping("/get-code")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    @Operation(
            summary = "Generate exam access code",
            description =
                    "Generates a short code for sharing/starting an exam. Returned code is string prefixed with 'K'.")
    public ResponseEntity<ApiResponse<String>> getExamAccessCode(@RequestParam UUID id) {
        Integer examCode = examService.getExamCode(id);
        return ResponseEntity.ok(
                ApiResponse.build(HttpStatus.OK, "Kod uğurla yaradıldı", "K" + examCode));
    }

    @GetMapping("/get-link")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    @Operation(
            summary = "Get exam access link",
            description =
                    "Get link for sharing an exam. Returned link with student if needed.")
    public ResponseEntity<ApiResponse<String>> getExamAccessLink(@RequestParam UUID id) {
        String link = examService.getExamLink(id);
        return ResponseEntity.ok(
                ApiResponse.build(HttpStatus.OK, "Link uğurla alındı", link));
    }

    @GetMapping("/start/code")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Start exam via code",
            description =
                    "Start/join an exam by providing access code (for students). Returns studentExamId and exam payload.")
    public ResponseEntity<ApiResponse<StartExamResponse>> startExam(@RequestParam(required = false) String studentName,
                                                                    @RequestParam String code) {
        StartExamResponse startExamResponse = examService.startExamViaCode(studentName, code);
        return ResponseEntity.ok(
                ApiResponse.build(HttpStatus.OK, "İmtahan uğurla başladı", startExamResponse));
    }

    @GetMapping("/start")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Start exam by UUID",
            description =
                    "Start an exam by providing its UUID (teacher/admin or system-start). Useful for scheduled or manual starts.")
    public ResponseEntity<ApiResponse<StartExamResponse>> startExam(@RequestParam(required = false) String studentName,
                                                                    @RequestParam UUID id) {
        StartExamResponse startExamResponse = examService.startExamViaId(studentName, id);
        return ResponseEntity.ok(
                ApiResponse.build(HttpStatus.OK, "İmtahan uğurla başladı", startExamResponse));
    }

    @PatchMapping("/finish")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Finish exam",
            description = "Finish an ongoing exam by providing studentExamId. Returns exam results and statistics."
    )
    public ResponseEntity<ApiResponse<ResultStatisticResponse>> finishExam(@RequestParam UUID studentExamId) {
        ResultStatisticResponse resultStatisticResponse = examService.finishExam(studentExamId);
        return ResponseEntity.ok(
                ApiResponse.build(HttpStatus.OK, "İmtahan uğurla başa çatdı", resultStatisticResponse));
    }

    @GetMapping("/result")
    @Operation(
            summary = "Get Exam Result",
            description = "Retrieve exam result statistics by providing studentExamId."
    )
    public ResponseEntity<ApiResponse<ResultStatisticResponse>> getResult(@RequestParam UUID studentExamId) {
        ResultStatisticResponse resultStatistic = examService.getResultStatistic(studentExamId);
        return ResponseEntity.ok(
                ApiResponse.build(HttpStatus.OK, "İmtahan nəticəsi uğurla əldə edildi", resultStatistic));
    }

    @PatchMapping("/publish")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    public ResponseEntity<ApiResponse<Void>> publish(@RequestParam UUID id) {
        examService.publishExam(id);
        return ResponseEntity.ok(
                ApiResponse.build(HttpStatus.OK, "İmtahan uğurla yayımlandı", null));
    }

    @PatchMapping("/unpublish")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    public ResponseEntity<ApiResponse<Void>> unpublish(@RequestParam UUID id) {
        examService.unpublishExam(id);
        return ResponseEntity.ok(
                ApiResponse.build(HttpStatus.OK, "İmtahan uğurla nəşrdən toplandı.", null));
    }

    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    @Operation(
            summary = "Update Exam",
            description =
                    "Update existing exam metadata and its media. ExamUpdateRequest must contain the exam UUID and updated ExamRequest payload. "
                            + "Send updated files as multipart lists (all files can be provided or only changed ones).")
    public ResponseEntity<ApiResponse<Void>> updateExam(
            @RequestPart @Valid ExamUpdateRequest request,
            @RequestPart(required = false) List<MultipartFile> titles,
            @RequestPart(required = false) List<MultipartFile> variantPictures,
            @RequestPart(required = false) List<MultipartFile> numberPictures,
            @RequestPart(required = false) List<MultipartFile> sounds) {
        examService.updateExam(request, titles, variantPictures, numberPictures, sounds);
        return ResponseEntity.ok(ApiResponse.build(HttpStatus.OK, "İmtahan uğurla yeniləndi", null));
    }

    @DeleteMapping
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    @Operation(
            summary = "Delete exam",
            description =
                    "Delete an exam by UUID. This operation typically marks exam as deleted or removes it permanently depending on service logic.")
    public ResponseEntity<ApiResponse<Void>> deleteExam(@RequestParam UUID id) {
        examService.deleteExam(id);
        return ResponseEntity.ok(ApiResponse.build(HttpStatus.NO_CONTENT, "İmtahan uğurla silindi", null));
    }

    @PatchMapping("/give-rating")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "İmtahana qiymət verin", description = "Tələbələrə imtahana qiymət verməyə imkan verir.")
    public ResponseEntity<ApiResponse<Void>> giveRating(@RequestParam UUID examId, @RequestParam Integer rating) {
        examService.giveRatingToExam(examId, rating);
        return ResponseEntity.ok(ApiResponse.build(HttpStatus.OK, "Rating verildi.", null));
    }
}
