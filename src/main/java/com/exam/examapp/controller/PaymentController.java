package com.exam.examapp.controller;

import com.exam.examapp.dto.response.ApiResponse;
import com.exam.examapp.dto.response.payment.PaymentResultResponse;
import com.exam.examapp.service.interfaces.payment.PaymentResultService;
import com.exam.examapp.service.interfaces.payment.PaymentService;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.UUID;

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
    public ResponseEntity<ApiResponse<String>> initPayment(@RequestParam @Min(0) @Max(10000) @Valid double amount,
                                                           @RequestParam UUID productId) throws NoSuchAlgorithmException, JsonProcessingException {
        String paymentUrl = paymentService.initPayment(amount, productId);
        return ResponseEntity.ok(ApiResponse.build(
                HttpStatus.OK,
                "Ödəniş uğurla başladı",
                paymentUrl));
    }

    @GetMapping("/success")
    @Operation(
            summary = "Payment Redirect",
            description = "This endpoint is called when the payment finish. Status is passed as query param."
    )
    public ResponseEntity<ApiResponse<String>> success() {
        return ResponseEntity.ok(ApiResponse.build(HttpStatus.OK, "Ödəniş nəticəsi", "Success"));
    }

    @GetMapping("/verify")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Payment verified",
            description = "This endpoint is called when the payment finish. Status update."
    )
    public ResponseEntity<ApiResponse<Void>> verify() {
        paymentResultService.updateResults();
        return ResponseEntity.ok(ApiResponse.build(HttpStatus.OK, "Ödəniş nəticəsi", null));
    }

    @GetMapping("/cancel")
    @Operation(
            summary = "Payment Redirect",
            description = "This endpoint is called when the payment finish. Status is passed as query param."
    )
    public ResponseEntity<ApiResponse<String>> cancel() {
        return ResponseEntity.ok(ApiResponse.build(HttpStatus.OK, "Ödəniş nəticəsi", "Cancelled"));
    }

    @GetMapping("/decline")
    @Operation(
            summary = "Payment Redirect",
            description = "This endpoint is called when the payment finish. Status is passed as query param."
    )
    public ResponseEntity<ApiResponse<String>> decline(@RequestParam String status) {
        return ResponseEntity.ok(ApiResponse.build(HttpStatus.OK, "Ödəniş nəticəsi", "Declined"));
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
                        "Ödəniş nəticələri uğurla əldə edildi",
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
                        "Ödəniş nəticələrim uğurla əldə edildi",
                        myPaymentResults));
    }
}