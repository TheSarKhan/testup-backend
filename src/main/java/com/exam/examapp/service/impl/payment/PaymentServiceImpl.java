package com.exam.examapp.service.impl.payment;

import com.exam.examapp.dto.request.payment.Body;
import com.exam.examapp.dto.request.payment.PaymentInitRequest;
import com.exam.examapp.dto.request.payment.PaymentRequest;
import com.exam.examapp.dto.response.payment.PaymentInitResponse;
import com.exam.examapp.dto.response.payment.PaymentInvoiceResponse;
import com.exam.examapp.exception.custom.ResourceNotFoundException;
import com.exam.examapp.model.Pack;
import com.exam.examapp.model.PaymentResult;
import com.exam.examapp.model.User;
import com.exam.examapp.model.enums.Role;
import com.exam.examapp.model.exam.Exam;
import com.exam.examapp.repository.PaymentResultRepository;
import com.exam.examapp.service.interfaces.*;
import com.exam.examapp.service.interfaces.exam.ExamService;
import com.exam.examapp.service.interfaces.exam.StudentExamService;
import com.exam.examapp.service.interfaces.payment.PaymentService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final UserService userService;

    private final PackService packService;

    private final ExamService examService;

    private final StudentExamService studentExamService;

    private final PaymentResultRepository paymentResultRepository;

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${payment.api.approve}")
    private String approveUrl;

    @Value("${payment.api.cancel}")
    private String cancelUrl;

    @Value("${payment.api.decline}")
    private String declineUrl;

    @Value("${payment.merchant}")
    private String merchant;

    @Value("${payment.secret.key}")
    private String secretKey;

    @Override
    @Transactional
    public String initPayment(PaymentRequest request) {
        String url = "https://api.payriff.com/api/v2/invoices";

        User user = userService.getCurrentUser();

        UUID productId = request.productId();
        String description = "Admin try to test";

        if (Role.TEACHER.equals(user.getRole())) {
            Pack pack = packService.getPackById(productId);
            description = pack.getPackName();
        } else if (Role.STUDENT.equals(user.getRole())) {
            Exam exam = examService.getById(productId);
            description = exam.getExamTitle();
        }

        PaymentInitRequest initRequest = new PaymentInitRequest();

        Body body = new Body();
        body.setAmount(request.amount());
        body.setCurrencyType(request.currency());
        body.setDescription(description);
        body.setFullName(user.getFullName());
        body.setEmail(user.getEmail());
        body.setPhoneNumber(user.getPhoneNumber());
        body.setApproveURL(approveUrl);
        body.setCancelURL(cancelUrl);
        body.setDeclineURL(declineUrl);
        body.setCustomMessage("Please check your payment result for submit app your payment.");
        body.setExpireDate(LocalDateTime.now().plusHours(1).format(DateTimeFormatter.ISO_DATE_TIME));
        body.setInstallmentPeriod(0);
        body.setInstallmentProductType("BIRKART");
        body.setLanguageType("EN");
        body.setSendSms(false);
        body.setSendWhatsapp(true);
        body.setSendEmail(false);
        body.setAmountDynamic(false);
        body.setDirectPay(true);
        body.setMetadata(
                Map.of("UserId", user.getId().toString(), "ProductId", productId.toString())
        );
        initRequest.setBody(body);
        initRequest.setMerchant(merchant);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", secretKey);

        HttpEntity<PaymentInitRequest> entity = new HttpEntity<>(initRequest, headers);

        ResponseEntity<PaymentInitResponse> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                PaymentInitResponse.class
        );

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            PaymentInitResponse.Payload payload = response.getBody().payload();
            paymentResultRepository.save(PaymentResult.builder()
                    .user(user).amount(request.amount())
                    .description(description).productId(request.productId())
                    .currency(request.currency()).invoiceUuid(payload.invoiceUuid())
                    .uuid(payload.uuid()).build());
            return payload.paymentUrl();
        } else {
            throw new RuntimeException("Failed to create payment: " +
                    (response.getBody() != null ? response.getBody().message() : "Unknown error"));
        }
    }

    @Override
    @Transactional
    public void updateResults(String uuid) {
        PaymentInvoiceResponse response = getInvoice(uuid);

        PaymentResult result = paymentResultRepository.getByInvoiceUuid(uuid).orElseThrow(() ->
                new ResourceNotFoundException("Payment result not found."));

        String status = response.payload().invoiceStatus();
        result.setStatus(status);
        result.setPaymentCreateDate(response.payload().createdDate());

        paymentResultRepository.save(result);

        if (status.equals("APPROVED")) {
            User user = userService.getCurrentUser();
            UUID productId = result.getProductId();
            if (Role.STUDENT.equals(user.getRole())) {
                studentExamService.addExam(user.getId(), productId);
            } else if (Role.TEACHER.equals(user.getRole())) {
                Pack pack = packService.getPackById(productId);
                user.setPack(pack);
                userService.save(user);
            }
        }
    }

    public PaymentInvoiceResponse getInvoice(String uuid) {
        String url = "https://api.payriff.com/api/v2/get-invoice";

        Map<String, Object> body = new HashMap<>();
        body.put("uuid", uuid);

        Map<String, Object> request = new HashMap<>();
        request.put("merchant", merchant);
        request.put("body", body);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", secretKey);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

        ResponseEntity<PaymentInvoiceResponse> response = restTemplate.postForEntity(url, entity, PaymentInvoiceResponse.class);

        System.out.println(response.getBody());
        return response.getBody();
    }
}
