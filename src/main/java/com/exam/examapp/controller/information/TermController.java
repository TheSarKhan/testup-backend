package com.exam.examapp.controller.information;

import com.exam.examapp.AppMessage;
import com.exam.examapp.dto.request.information.TermRequest;
import com.exam.examapp.dto.request.information.TermUpdateRequest;
import com.exam.examapp.dto.response.ApiResponse;
import com.exam.examapp.model.information.Term;
import com.exam.examapp.service.interfaces.information.TermService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/term")
@Tag(name = "Terms Management", description = "Endpoints for managing academic terms")
public class TermController {
    private final TermService termService;

    @PostMapping
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Create a new term",
            description = "This endpoint allows an **ADMIN** to create a new term by providing its name and description."
    )
    public ResponseEntity<ApiResponse<String>> createTerm(@RequestBody
                                                          @Valid
                                                          TermRequest request) {
        String result = termService.createTerm(request);
        return ResponseEntity.ok(
                ApiResponse.build(
                        HttpStatus.CREATED,
                        result,
                        null));
    }

    @GetMapping
    @Operation(
            summary = "Retrieve all terms",
            description = "Fetches a list of all available terms."
    )
    public ResponseEntity<ApiResponse<List<Term>>> getAll() {
        List<Term> allTerms = termService.getAllTerms();
        return ResponseEntity.ok(
                ApiResponse.build(
                        HttpStatus.OK,
                        AppMessage.TERM_RETRIEVE_SUCCESS.getMessage(),
                        allTerms));
    }

    @GetMapping("/name")
    @Operation(
            summary = "Get a term by name",
            description = "Retrieve a specific term by providing its name."
    )
    public ResponseEntity<ApiResponse<Term>> getByName(@RequestParam
                                                       @NotBlank
                                                       @Schema(defaultValue = "Term Name")
                                                       String name) {
        Term termByName = termService.getTermByName(name);
        return ResponseEntity.ok(
                ApiResponse.build(
                        HttpStatus.OK,
                        AppMessage.TERM_RETRIEVE_SUCCESS.getMessage(),
                        termByName));
    }

    @PutMapping
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Update an existing term",
            description = "This endpoint allows an **ADMIN** to update a term by its ID."
    )
    public ResponseEntity<ApiResponse<String>> update(@RequestBody
                                                      @Valid
                                                      TermUpdateRequest request) {
        String result = termService.updateTerm(request);
        return ResponseEntity.ok(
                ApiResponse.build(
                        HttpStatus.OK,
                        result,
                        null));
    }

    @DeleteMapping
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Delete a term",
            description = "This endpoint allows an **ADMIN** to delete a term by its ID."
    )
    public ResponseEntity<ApiResponse<String>> delete(@RequestParam
                                                      @NotNull
                                                      UUID id) {
        String term = termService.deleteTerm(id);
        return ResponseEntity.ok(
                ApiResponse.build(
                        HttpStatus.NO_CONTENT,
                        term,
                        null));
    }
}
