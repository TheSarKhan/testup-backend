package com.exam.examapp.security.service;

import com.exam.examapp.model.User;
import com.exam.examapp.security.dto.response.TokenResponse;
import com.exam.examapp.security.service.impl.JwtService;
import com.exam.examapp.service.interfaces.CacheService;
import com.exam.examapp.service.interfaces.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OAuth2Service {
    private static final String HEADER = "oauth2_uuid_";

    private final CacheService cacheService;

    private final JwtService jwtService;

    private final UserService userService;

    @Transactional
    public TokenResponse getToken(String id) {
        String email = cacheService.getContent(HEADER, id);
        cacheService.deleteContent(HEADER, id);

        String accessToken = jwtService.generateAccessToken(email);
        String refreshToken = jwtService.generateRefreshToken(email);

        User user = userService.getByEmail(email);

        return new TokenResponse(accessToken, refreshToken, user.getRole(), user.getPack());
    }
}
