package com.exam.examapp.service.impl.payment;

import com.exam.examapp.dto.response.payment.PaymentInitResponse;
import com.exam.examapp.model.Pack;
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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class EPointServiceImpl implements PaymentService {
    private final UserService userService;

    private final PackService packService;

    private final ExamService examService;

    private final StudentExamService studentExamService;

    private final PaymentResultRepository paymentResultRepository;

    private final LogService logService;

    private final RestTemplate restTemplate = new RestTemplate();

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${payment.api.approve}")
    private String approveUrl;

    @Value("${payment.api.decline}")
    private String declineUrl;

    @Value("${payment.private-key}")
    private String privateKey;

    @Value("${payment.public-key}")
    private String publicKey;

    @Override
    @Transactional
    public String initPayment(double amount, UUID productId) throws JsonProcessingException, NoSuchAlgorithmException {
        log.info("Ödəniş yaradılır");
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("public_key", publicKey);
        jsonMap.put("amount", amount);
        jsonMap.put("currency", "AZN");
        jsonMap.put("language", "az");
        jsonMap.put("order_id", UUID.randomUUID().toString());
        jsonMap.put("description", getDescription(userService.getCurrentUser(), productId));
        jsonMap.put("success_redirect_url", approveUrl);
        jsonMap.put("error_redirect_url", declineUrl);
        jsonMap.put("other_attr", "[" + productId.toString() + ", " + userService.getCurrentUser().getId() + "]");

        String jsonString = objectMapper.writeValueAsString(jsonMap);

        String data = Base64.getEncoder()
                .encodeToString(jsonString.getBytes(StandardCharsets.UTF_8));

        String signString = privateKey + data + privateKey;

        MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
        byte[] hashBytes = sha1.digest(signString.getBytes(StandardCharsets.UTF_8));

        String signature = Base64.getEncoder().encodeToString(hashBytes);

        String response = sendRequestToEpoint(data, signature);
        log(amount, userService.getCurrentUser(), objectMapper.readValue(response, PaymentInitResponse.class).payload());
        return response;
    }

    public String sendRequestToEpoint(String data, String signature) {
        String url = "https://epoint.az/api/1/request";

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("data", data);
        body.add("signature", signature);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> requestEntity =
                new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        return response.getBody();
    }


    //    public String initPayment(PaymentRequest paymentRequest) {
//
//
//
//       if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
//          PaymentInitResponse.Payload payload = response.getBody().payload();
//           savePaymentResult(paymentRequest, user, description, payload);
//
//           log(paymentRequest, user, payload);
//           return payload.paymentUrl();
//      } else {
//           throw new RuntimeException("Ödəniş yaratmaq alınmadı: " +
//                   (response.getBody() != null ? response.getBody().message() : "Naməlum xəta"));
//       }
//    }
    @Override
    public void updateResults(String uuid) {
//        log.info("Ödəniş yenilənir");
//        PaymentInvoiceResponse response = getInvoice(uuid);
//
//        PaymentResult result = paymentResultRepository.getByInvoiceUuid(uuid).orElseThrow(() ->
//                new ResourceNotFoundException("Ödəniş nəticəsi tapılmadı"));
//
//        String status = response.payload().invoiceStatus();
//        result.setStatus(status);
//        result.setPaymentCreateDate(response.payload().createdDate());
//
//        paymentResultRepository.save(result);
//
//        if (status.equals("APPROVED")) {
//            User user = result.getUser();
//            UUID productId = result.getProductId();
//            if (Role.STUDENT.equals(user.getRole())) {
//                studentExamService.addExam(user.getId(), productId);
//                log.info("Tələbə imtahan aldı. Imtahan id-si: {}", productId);
//                logService.save("Tələbə imtahan aldı. Imtahan id-si: " + productId, userService.getCurrentUserOrNull());
//            } else if (Role.TEACHER.equals(user.getRole())) {
//                Pack pack = packService.getPackById(productId);
//                user.setPack(pack);
//                user.setNextPaymentDate(Instant.now().plusSeconds(2_629_743));
//                userService.update(user);
//                log.info("Müəllim yeni paket aldı. Paketin adı: {}", pack.getPackName());
//                logService.save("Müəllim yeni paket aldı. Paketin adı: " + pack.getPackName(), userService.getCurrentUserOrNull());
//            }
//        }
    }

    private String getDescription(User user, UUID productId) {
        String description = "Admin sınamağa çalışır";

        if (Role.TEACHER.equals(user.getRole())) {
            Pack pack = packService.getPackById(productId);
            description = pack.getPackName();
        } else if (Role.STUDENT.equals(user.getRole())) {
            Exam exam = examService.getById(productId);
            description = exam.getExamTitle();
        }
        return description;
    }

    private void log(double amount, User user, PaymentInitResponse.Payload payload) {
        String message = "Ödəniş məlumatı. Email : " + user.getEmail() +
                " , Məbləğ : " + amount + " , Valyuta : AZN" +
                " , Invoice UUID : " + payload.invoiceUuid();
        log.info(message);
        logService.save(message, user);
    }
}
