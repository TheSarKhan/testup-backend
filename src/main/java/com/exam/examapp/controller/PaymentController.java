package com.exam.examapp.controller;

import com.exam.examapp.dto.request.payment.PaymentCallbackRequest;
import com.exam.examapp.dto.request.payment.PaymentRequest;
import com.exam.examapp.dto.response.ApiResponse;
import com.exam.examapp.dto.response.PaymentResultResponse;
import com.exam.examapp.service.interfaces.PaymentResultService;
import com.exam.examapp.service.interfaces.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payment")
@Tag(name = "Payment", description = "Endpoints for managing payments and results")
public class PaymentController {
    private final PaymentService paymentService;

    private final PaymentResultService paymentResultService;

    @PostMapping("/init")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Initialize payment",
            description = "Starts the payment process and returns the payment URL."
    )
    public ResponseEntity<ApiResponse<String>> initPayment(@RequestBody @Valid PaymentRequest request) {
        String paymentUrl = paymentService.initPayment(request);
        return ResponseEntity.ok(ApiResponse.build(
                HttpStatus.OK,
                "Payment initiated successfully",
                paymentUrl));
    }

    @GetMapping("/success")
    @Operation(
            summary = "Payment Redirect",
            description = "This endpoint is called when the payment finish. Status is passed as query param."
    )
    public ResponseEntity<ApiResponse<String>> success() {
        return ResponseEntity.ok(ApiResponse.build(HttpStatus.OK, "Payment result", "Success"));
    }

    @GetMapping("/cancel")
    @Operation(
            summary = "Payment Redirect",
            description = "This endpoint is called when the payment finish. Status is passed as query param."
    )
    public ResponseEntity<ApiResponse<String>> cancel() {
        return ResponseEntity.ok(ApiResponse.build(HttpStatus.OK, "Payment result", "Cancelled"));
    }

    @GetMapping("/decline")
    @Operation(
            summary = "Payment Redirect",
            description = "This endpoint is called when the payment finish. Status is passed as query param."
    )
    public ResponseEntity<ApiResponse<String>> decline(@RequestParam String status) {
        return ResponseEntity.ok(ApiResponse.build(HttpStatus.OK, "Payment result", "Declined"));
    }

    @GetMapping
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Get all payment results",
            description = "Retrieves all payment results. Accessible only by ADMIN."
    )
    public ResponseEntity<ApiResponse<List<PaymentResultResponse>>> getAll() {
        List<PaymentResultResponse> allPaymentResults = paymentResultService.getAllPaymentResults();
        return ResponseEntity.ok(
                ApiResponse.build(
                        HttpStatus.OK,
                        "Payment results retrieved successfully",
                        allPaymentResults));
    }

    @GetMapping("/my")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Get my payment results",
            description = "Retrieves payment results for the currently authenticated user."
    )
    public ResponseEntity<ApiResponse<List<PaymentResultResponse>>> getMyPayments() {
        List<PaymentResultResponse> myPaymentResults = paymentResultService.getMyPaymentResults();
        return ResponseEntity.ok(
                ApiResponse.build(
                        HttpStatus.OK,
                        "My Payment results retrieved successfully",
                        myPaymentResults));
    }
}