package com.exam.examapp.security.config;

import com.exam.examapp.security.filter.CustomJwtFilter;
import com.exam.examapp.security.oauth2.CustomOAuth2UserService;
import com.exam.examapp.security.oauth2.OAuth2LoginFailureHandler;
import com.exam.examapp.security.oauth2.OAuth2LoginSuccessHandler;
import com.exam.examapp.security.service.impl.CustomUserDetailsService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final CustomUserDetailsService userDetailsService;

    private final CustomJwtFilter jwtFilter;

    private final CustomOAuth2UserService customOAuth2UserService;

    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

    private final OAuth2LoginFailureHandler oAuth2LoginFailureHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity security) throws Exception {
        security.csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((req, res, e) -> {
                            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            res.setContentType("application/json");
                            res.getWriter().write("{\"error\": \"Invalid or missing token\"}");
                        })
                        .accessDeniedHandler((req, res, e) -> {
                            res.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            res.setContentType("application/json");
                            res.getWriter().write("{\"error\": \"Access denied\"}");
                        }))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**")
                        .permitAll()
                        .requestMatchers(
                                "/swagger-ui/**", "/v3/api-docs/**",
                                "/login/oauth2/**", "/oauth2/authorization/**",
                                "/home", "/api/v1/auth/**",
                                "/uploads/**", "/api/v1/about",
                                "/api/v1/header", "/api/v1/test/**",
                                "/api/v1/home", "/api/v1/payment/success",
                                "/api/v1/payment/cancel", "/api/v1/payment/decline",
                                "/api/v1/oauth2/login", "/api/v1/exam/all",
                                "/api/v1/exam/get-link", "/api/v1/exam/tags",
                                "/api/v1/exam/last-created", "/api/v1/exam/start/code",
                                "/api/v1/exam/finish", "/api/v1/exam/start-info/**",
                                "/api/v1/exam/result", "/api/v1/exam/detailed/**",
                                "/api/v1/exam/start/**", "/api/v1/student-exam/answer",
                                "/api/v1/student-question/**"
                        ).permitAll()
                        .requestMatchers(HttpMethod.GET,
                                "/api/v1/tag/**", "/api/v1/pack/**",
                                "/api/v1/term/**", "/api/v1/advertisement/**",
                                "/api/v1/media-content/**", "/api/v1/superiority/**",
                                "/api/v1/contact/**"
                        ).permitAll()
                        .requestMatchers("/api/v1/auth/finish-register").authenticated()
                        .anyRequest().authenticated())
                .oauth2Login(oauth -> oauth
                        .userInfoEndpoint(user -> user.oidcUserService(customOAuth2UserService))
                        .successHandler(oAuth2LoginSuccessHandler)
                        .failureHandler(oAuth2LoginFailureHandler));

        return security.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOriginPattern("https://*.testup.az");
        configuration.addAllowedOrigin("https://localhost:5173");
        configuration.addAllowedOrigin("http://localhost:5173");
        configuration.addAllowedOrigin("http://45.87.120.60:8080");
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
