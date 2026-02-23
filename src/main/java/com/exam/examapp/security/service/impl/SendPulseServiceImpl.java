package com.exam.examapp.security.service.impl;

import com.exam.examapp.dto.request.MultiEmailRequest;
import com.exam.examapp.security.dto.SendPlusRequest;
import com.exam.examapp.security.dto.SendPulseTokenResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class SendPulseServiceImpl {
    private static final String TOKEN_URL = "https://api.sendpulse.com/oauth/access_token";

    private static final String EMAIL_URL = "https://api.sendpulse.com/smtp/emails";

    @Value("${sendpulse.userid}")
    private String userId;

    @Value("${sendpulse.secret}")
    private String secret;

    @Value("${sendpulse.sender}")
    private String sender;

    private final RestTemplate restTemplate = new RestTemplate();

    private String getAccessToken() {
        Map<String, String> body = Map.of(
                "grant_type", "client_credentials",
                "client_id", userId,
                "client_secret", secret
        );

        SendPulseTokenResponse response =
                restTemplate.postForObject(
                        TOKEN_URL,
                        body,
                        SendPulseTokenResponse.class
                );

        assert response != null;
        return response.access_token();
    }

    public String sendEmail(String to, String subject, String content) {
        String token = getAccessToken();

        String encodedHtml = Base64.getEncoder()
                .encodeToString(content.getBytes());

        SendPlusRequest request = new SendPlusRequest(
                new SendPlusRequest.Email(
                        encodedHtml,
                        content,
                        subject,
                        new SendPlusRequest.From("Exam App", sender),
                        List.of(
                                new SendPlusRequest.To("", to)
                        )
                )
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<SendPlusRequest> entity =
                new HttpEntity<>(request, headers);

        ResponseEntity<String> response =
                restTemplate.exchange(
                        EMAIL_URL,
                        HttpMethod.POST,
                        entity,
                        String.class
                );

        log.info("SendPulse response: {}", response.getBody());

        return response.getBody();
    }

    public void sendEmailToAll(MultiEmailRequest request) {

        String token = getAccessToken();

        String encodedHtml = Base64.getEncoder()
                .encodeToString(request.message().getBytes());

        List<SendPlusRequest.To> recipients =
                request.emails()
                        .stream()
                        .map(email -> new SendPlusRequest.To("", email))
                        .toList();

        SendPlusRequest sendRequest = new SendPlusRequest(
                new SendPlusRequest.Email(
                        encodedHtml,
                        request.message(),
                        request.title(),
                        new SendPlusRequest.From("Exam App", sender),
                        recipients
                )
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<SendPlusRequest> entity =
                new HttpEntity<>(sendRequest, headers);

        restTemplate.exchange(
                EMAIL_URL,
                HttpMethod.POST,
                entity,
                String.class
        );

        log.info("Multi email sent successfully");
    }
}
