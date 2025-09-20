package com.exam.examapp.security.oauth2;

import com.exam.examapp.security.service.impl.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {
    private final JwtService jwtService;

    @Value("${app.front-base-url}")
    private String baseUrl;

    @Value("${app.oauth-security}")
    private boolean oauthSecurity;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OidcUser oidcUser = (OidcUser) authentication.getPrincipal();
        String email = oidcUser.getEmail();
        Map<String, Object> claims = oidcUser.getClaims();

        String accessToken = jwtService.generateAccessToken(email);
        String refreshToken = jwtService.generateRefreshToken(email);

        ResponseCookie accessCookie = ResponseCookie.from("accessToken", accessToken)
                .secure(true)
                .path("/")
                .maxAge(3600)
                .sameSite("None")
                .build();
        response.addHeader("Set-Cookie", accessCookie.toString());

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
                .secure(true)
                .path("/")
                .maxAge(24 * 3600)
                .sameSite("None")
                .build();
        response.addHeader("Set-Cookie", refreshCookie.toString());

        Cookie cookie = new Cookie("role", String.valueOf(claims.get("role")));
        cookie.setPath("/");
        cookie.setHttpOnly(false);
        cookie.setSecure(true);
        cookie.setMaxAge(60 * 60);
        response.addCookie(cookie);


        ResponseCookie packCookie = ResponseCookie.from("pack", String.valueOf(claims.get("pack")))
                .secure(oauthSecurity)
                .path("/")
                .maxAge(3600)
                .sameSite("None")
                .build();
        response.addHeader("Set-Cookie", packCookie.toString());


        response.sendRedirect(baseUrl);
    }
}
