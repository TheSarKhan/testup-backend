package com.exam.examapp.security.oauth2;

import com.exam.examapp.security.service.impl.JwtService;
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

    @Value("${app.base-url}")
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

        ResponseCookie accessCookie = ResponseCookie.from("ACCESS_TOKEN", accessToken)
                .httpOnly(true)
                .secure(oauthSecurity)
                .path("/")
                .maxAge(3600)
                .sameSite("Strict")
                .build();
        response.addHeader("Set-Cookie", accessCookie.toString());

        ResponseCookie refreshCookie = ResponseCookie.from("REFRESH_TOKEN", refreshToken)
                .httpOnly(true)
                .secure(oauthSecurity)
                .path("/")
                .maxAge(24 * 3600)
                .sameSite("Strict")
                .build();
        response.addHeader("Set-Cookie", refreshCookie.toString());

        ResponseCookie roleCookie = ResponseCookie.from("ROLE", String.valueOf(claims.get("role")))
                .httpOnly(false)
                .secure(oauthSecurity)
                .path("/")
                .maxAge(3600)
                .sameSite("Strict")
                .build();
        response.addHeader("Set-Cookie", roleCookie.toString());

        ResponseCookie packCookie = ResponseCookie.from("PACK", String.valueOf(claims.get("pack")))
                .httpOnly(false)
                .secure(oauthSecurity)
                .path("/")
                .maxAge(3600)
                .sameSite("Strict")
                .build();
        response.addHeader("Set-Cookie", packCookie.toString());

        response.sendRedirect(baseUrl + "/oauth2/success");
    }
}
