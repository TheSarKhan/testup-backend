package com.exam.examapp.security.oauth2;

import com.exam.examapp.service.interfaces.CacheService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {
    private static final String HEADER = "oauth2_uuid_";

    private final CacheService cacheService;

    @Value("${app.front-base-url}")
    private String baseUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OidcUser oidcUser = (OidcUser) authentication.getPrincipal();
        String email = oidcUser.getEmail();

        UUID id = UUID.randomUUID();

        cacheService.saveContent(HEADER, id.toString(), email, (long) 5 * 60 * 1000);

        response.sendRedirect(baseUrl + "/" + id);
    }
}
