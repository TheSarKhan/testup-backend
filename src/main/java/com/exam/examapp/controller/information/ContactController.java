package com.exam.examapp.controller.information;

import com.exam.examapp.dto.request.information.ContactUpdateRequest;
import com.exam.examapp.dto.response.ApiResponse;
import com.exam.examapp.model.information.Contact;
import com.exam.examapp.service.interfaces.information.ContactService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/contact")
@Tag(name = "Contact Management", description = "Endpoints for managing contact information")
public class ContactController {
    private final ContactService contactService;

    @PutMapping
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Update contact information",
            description = "This endpoint allows an **ADMIN** to update the contact details such as phone, email, address, and social links."
    )
    public ResponseEntity<ApiResponse<Void>> update(@RequestPart
                                                    @Valid
                                                    ContactUpdateRequest request,
                                                    @RequestPart(required = false)
                                                    List<MultipartFile> icons) {
        contactService.updateContact(request, icons);
        return ResponseEntity.ok(
                ApiResponse.build(
                        HttpStatus.OK,
                        "Contact updated successfully",
                        null));
    }

    @GetMapping
    @Operation(
            summary = "Retrieve contact information",
            description = "Fetches the complete contact information including phone, email, address, and social links."
    )
    public ResponseEntity<ApiResponse<Contact>> get() {
        Contact contactResponse = contactService.getContact();
        return ResponseEntity.ok(
                ApiResponse.build(
                        HttpStatus.OK,
                        "Contact retrieved successfully",
                        contactResponse));
    }
}
