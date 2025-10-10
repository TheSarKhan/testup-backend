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
import com.exam.examapp.service.interfaces.LogService;
import com.exam.examapp.service.interfaces.PackService;
import com.exam.examapp.service.interfaces.UserService;
import com.exam.examapp.service.interfaces.exam.ExamService;
import com.exam.examapp.service.interfaces.exam.StudentExamService;
import com.exam.examapp.service.interfaces.payment.PaymentService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final UserService userService;

    private final PackService packService;

    private final ExamService examService;

    private final StudentExamService studentExamService;

    private final PaymentResultRepository paymentResultRepository;

    private final LogService logService;

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
        log.info("Ödəniş yaradılır");
        String url = "https://api.payriff.com/api/v2/invoices";

        User user = userService.getCurrentUser();

        UUID productId = request.productId();
        String description = "Admin sınamağa çalışır";

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
        body.setCustomMessage("Ödənişinizi proqrama təqdim etmək üçün ödəniş nəticəsini yoxlayın.");
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

            log.info("Ödəniş məlumatı. Email : {} , Məbləğ : {} , Valyuta : {} , Invoice UUID : {}",
                    user.getEmail(), request.amount(), request.currency(), payload.invoiceUuid());
            return payload.paymentUrl();
        } else {
            throw new RuntimeException("Ödəniş yaratmaq alınmadı: " +
                    (response.getBody() != null ? response.getBody().message() : "Naməlum xəta"));
        }
    }

    @Override
    @Transactional
    public void updateResults(String uuid) {
        log.info("Ödəniş yenilənir");
        PaymentInvoiceResponse response = getInvoice(uuid);

        PaymentResult result = paymentResultRepository.getByInvoiceUuid(uuid).orElseThrow(() ->
                new ResourceNotFoundException("Ödəniş nəticəsi tapılmadı"));

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
                user.setNextPaymentDate(Instant.now().plusSeconds(2_629_743));
                userService.save(user);
            }
        }
        log.info("Ödəniş yeniləndi");
        logService.save("Ödəniş yeniləndi", userService.getCurrentUserOrNull());
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
