package com.exam.examapp.security.service.impl;

import com.exam.examapp.AppMessage;
import com.exam.examapp.exception.custom.BadRequestException;
import com.exam.examapp.exception.custom.InvalidCredentialsException;
import com.exam.examapp.exception.custom.ResourceNotFoundException;
import com.exam.examapp.model.TeacherInfo;
import com.exam.examapp.model.User;
import com.exam.examapp.model.enums.Role;
import com.exam.examapp.security.dto.request.LoginRequest;
import com.exam.examapp.security.dto.request.RegisterRequest;
import com.exam.examapp.security.dto.request.ResetPasswordRequest;
import com.exam.examapp.security.dto.response.TokenResponse;
import com.exam.examapp.security.service.interfaces.AuthService;
import com.exam.examapp.security.service.interfaces.EmailService;
import com.exam.examapp.service.interfaces.CacheService;
import com.exam.examapp.service.interfaces.PackService;
import com.exam.examapp.service.interfaces.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final String REFRESH_TOKEN_HEADER = "refresh_token_";

    private final String FORGET_PASSWORD_HEADER = "refresh_token_";

    private final String ACCESS_RESET_PASSWORD = "access_reset_password_";
    private final UserService userService;
    private final CacheService cacheService;
    private final JwtService jwtService;
    private final EmailService emailService;
    private final PackService packService;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.name}")
    private String appName;

    @Override
    @Transactional
    public String register(RegisterRequest request) {
        if (!request.isAcceptTerms())
            throw new BadRequestException(AppMessage.TERMS_NOT_ACCEPTED.getMessage());

        if (request.role() == Role.ADMIN)
            throw new BadRequestException(AppMessage.ADMIN_CANNOT_BE_REGISTERED.getMessage());

        if (userService.existsByEmail(request.email()))
            throw new BadRequestException(AppMessage.USER_EXISTS_WITH_EMAIL.format(request.email()));

        if (userService.existsByPhoneNumber(request.phoneNumber()))
            throw new BadRequestException(
                    AppMessage.USER_EXISTS_WITH_PHONE.format(request.phoneNumber()));

        User user =
                User.builder()
                        .email(request.email())
                        .role(request.role())
                        .pack(Role.TEACHER.equals(request.role()) ? packService.getPackByName("Free") : null)
                        .password(passwordEncoder.encode(request.password()))
                        .fullName(request.fullName())
                        .phoneNumber(request.phoneNumber())
                        .isAcceptedTerms(true)
                        .info(
                                Role.TEACHER.equals(request.role())
                                        ? new TeacherInfo(Instant.now(), 0, 0, new HashMap<>())
                                        : null)
                        .build();
        userService.save(user);
        return AppMessage.USER_REGISTERED_SUCCESS.format(user.getFullName());
    }

    @Override
    @Transactional
    public TokenResponse login(LoginRequest request) {
        User user = userService.getByEmail(request.email());

        if (!user.isAcceptedTerms())
            throw new BadRequestException(AppMessage.TERMS_NOT_ACCEPTED.getMessage());

        if (!passwordEncoder.matches(request.password(), user.getPassword()))
            throw new InvalidCredentialsException(AppMessage.INVALID_CREDENTIALS.getMessage());

        if (!user.isActive() || user.isDeleted())
            throw new InvalidCredentialsException(AppMessage.USER_NOT_ACTIVE_OR_DELETED.getMessage());

        String accessToken = jwtService.generateAccessToken(request.email());
        String refreshToken = jwtService.generateRefreshToken(request.email());

        log.info(AppMessage.USER_LOGGED_IN_SUCCESS.getMessage());
        return new TokenResponse(accessToken, refreshToken, user.getRole(), user.getPack());
    }

    @Override
    public String logout(String email) {
        cacheService.deleteContent(REFRESH_TOKEN_HEADER, email);

        return AppMessage.USER_LOGGED_OUT_SUCCESS.getMessage();
    }

    @Override
    @Transactional
    public TokenResponse refresh(String refreshToken) {
        String email = jwtService.extractEmail(refreshToken);
        if (email == null)
            throw new InvalidCredentialsException(AppMessage.INVALID_REFRESH_TOKEN.getMessage());

        cacheService.deleteContent(REFRESH_TOKEN_HEADER, email);

        String accessToken = jwtService.generateAccessToken(email);
        String newRefreshToken = jwtService.generateRefreshToken(email);

        User user = userService.getByEmail(email);
        return new TokenResponse(accessToken, newRefreshToken, user.getRole(), user.getPack());
    }

    @Override
    public String forgetPassword(String email) {
        int randomCode = (int) (Math.random() * 9000) + 1000;
        if (!userService.existsByEmail(email))
            throw new ResourceNotFoundException(AppMessage.EMAIL_NOT_FOUND.getMessage());

        String emailResponse =
                emailService.sendEmail(
                        email, appName + " Verification Code", AppMessage.EMAIL_SENT.format(randomCode));
        cacheService.saveContent(
                FORGET_PASSWORD_HEADER, email, String.valueOf(randomCode), (long) (2 * 60 * 1000));

        return emailResponse;
    }

    @Override
    public String verifyEmailCode(String email, String code) {
        String redisCode = cacheService.getContent(FORGET_PASSWORD_HEADER, email);

        if (redisCode == null || !redisCode.equals(code))
            throw new BadRequestException(AppMessage.INVALID_CODE.getMessage());

        cacheService.deleteContent(FORGET_PASSWORD_HEADER, email);
        UUID uuid = UUID.randomUUID();
        cacheService.saveContent(ACCESS_RESET_PASSWORD, email, uuid.toString(), (long) 10 * 60 * 1000);
        return uuid.toString();
    }

    @Override
    public String resetPassword(ResetPasswordRequest request) {
        String redisUuid = cacheService.getContent(ACCESS_RESET_PASSWORD, request.email());
        if (redisUuid == null || !redisUuid.equals(request.uuid()))
            return AppMessage.DONT_COPY_PASTE_LINK.getMessage();

        cacheService.deleteContent(ACCESS_RESET_PASSWORD, request.email());
        User user = userService.getByEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        userService.save(user);
        return AppMessage.PASSWORD_RESET_SUCCESS.getMessage();
    }

    @Override
    public TokenResponse resetCurrentUserPassword(String oldPassword, String newPassword) {
        User user = userService.getCurrentUser();
        if (!passwordEncoder.matches(oldPassword, user.getPassword()))
            throw new InvalidCredentialsException("Old password is incorrect");

        user.setPassword(passwordEncoder.encode(newPassword));
        userService.save(user);

        String email = user.getEmail();

        return new TokenResponse(jwtService.generateAccessToken(email),
                jwtService.generateRefreshToken(email), user.getRole(), user.getPack());
    }

    @Override
    public TokenResponse finishRegister(Role role, boolean isAcceptedTerms) {
        if (!isAcceptedTerms) throw new BadRequestException(AppMessage.TERMS_NOT_ACCEPTED.getMessage());

        User currentUser = userService.getCurrentUser();
        if (!Role.EMPTY.equals(currentUser.getRole()))
            throw new BadRequestException("User already registered.");

        if (Role.ADMIN.equals(role) || Role.EMPTY.equals(role))
            throw new BadRequestException("Role cannot be empty or admin.");

        currentUser.setPack(Role.TEACHER.equals(role) ? packService.getPackByName("Free") : null);

        currentUser.setInfo(
                Role.TEACHER.equals(role) ? new TeacherInfo(Instant.now(), 0, 0, new HashMap<>()) : null);

        currentUser.setRole(role);
        userService.save(currentUser);

        String accessToken = jwtService.generateAccessToken(currentUser.getEmail());
        String refreshToken = jwtService.generateRefreshToken(currentUser.getEmail());

        return new TokenResponse(accessToken, refreshToken, currentUser.getRole(), currentUser.getPack());
    }
}
