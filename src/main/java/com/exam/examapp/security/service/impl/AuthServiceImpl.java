package com.exam.examapp.security.service.impl;

import com.exam.examapp.exception.custom.BadRequestException;
import com.exam.examapp.exception.custom.InvalidCredentialsException;
import com.exam.examapp.exception.custom.ResourceNotFoundException;
import com.exam.examapp.model.TeacherInfo;
import com.exam.examapp.model.User;
import com.exam.examapp.model.enums.Role;
import com.exam.examapp.model.question.QuestionStorage;
import com.exam.examapp.repository.QuestionStorageRepository;
import com.exam.examapp.security.dto.request.LoginRequest;
import com.exam.examapp.security.dto.request.RegisterRequest;
import com.exam.examapp.security.dto.request.ResetPasswordRequest;
import com.exam.examapp.security.dto.response.TokenResponse;
import com.exam.examapp.security.service.interfaces.AuthService;
import com.exam.examapp.security.service.interfaces.EmailService;
import com.exam.examapp.service.interfaces.CacheService;
import com.exam.examapp.service.interfaces.LogService;
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

    private final String FORGET_PASSWORD_HEADER = "forget_password_";

    private final String ACCESS_RESET_PASSWORD = "access_reset_password_";

    private final UserService userService;
    private final CacheService cacheService;
    private final JwtService jwtService;
    private final EmailService emailService;
    private final PackService packService;
    private final PasswordEncoder passwordEncoder;
    private final LogService logService;
    private final QuestionStorageRepository questionStorageRepository;

    @Value("${app.default-pack-name}")
    private String defaultPackName;

    @Value("${app.name}")
    private String appName;

    @Override
    @Transactional
    public String register(RegisterRequest request) {
        log.info("İstifadəçi qeydiyyatdan keçir:{}", request.email());
        if (!request.isAcceptTerms())
            throw new BadRequestException("İstifadəçi şərtləri qəbul etmədi");

        if (request.role() == Role.ADMIN || request.role() == Role.EMPTY)
            throw new BadRequestException("İstifadəçi rolu admin və ya empty ola bilməz");

        if (userService.existsByEmail(request.email()))
            throw new BadRequestException("Email artıq mövcuddur");

        if (userService.existsByPhoneNumber(request.phoneNumber()))
            throw new BadRequestException("Telefon nömrəsi mövcuddur");

        User user =
                User.builder()
                        .email(request.email())
                        .role(request.role())
                        .pack(Role.TEACHER.equals(request.role()) ? packService.getPackByName(defaultPackName) : null)
                        .password(passwordEncoder.encode(request.password()))
                        .fullName(request.fullName())
                        .phoneNumber(request.phoneNumber())
                        .isAcceptedTerms(true)
                        .info(
                                Role.TEACHER.equals(request.role())
                                        ? new TeacherInfo(Instant.now(), 0, 0, new HashMap<>())
                                        : null)
                        .build();

        User save = userService.save(user);

        if (Role.TEACHER.equals(request.role()))
            questionStorageRepository.save(QuestionStorage.builder().teacher(save).build());

        String message = Role.TEACHER.equals(request.role()) ? "Müəllim " : "Tələbə ";
        message = message.concat("qeydiyyatdan keçdi: ").concat(request.fullName());
        log.info(message);
        logService.save(message, save);
        return message;
    }

    @Override
    @Transactional
    public TokenResponse login(LoginRequest request) {
        log.info("İstifadəçi login olur:{}", request.email());
        User user = userService.getByEmail(request.email());

        if (!user.isAcceptedTerms())
            throw new BadRequestException("İstifadəçi şərtləri qəbul etməyib");

        if (!passwordEncoder.matches(request.password(), user.getPassword()))
            throw new InvalidCredentialsException("Parol yanlışdır");

        if (!user.isActive() || user.isDeleted())
            throw new InvalidCredentialsException("İstifadəçi aktif deyil və yaxud silinib");

        String accessToken = jwtService.generateAccessToken(request.email());
        String refreshToken = jwtService.generateRefreshToken(request.email());

        log.info("İstifadəçi login oldu:{}", request.email());
        return new TokenResponse(accessToken, refreshToken, user.getRole(), user.getPack());
    }

    @Override
    public String logout(String email) {
        log.info("İstifadəçi logout olur:{}", email);
        cacheService.deleteContent(REFRESH_TOKEN_HEADER, email);
        log.info("İstifadəçi logout oldu");
        return "İstifadəçi logout oldu";
    }

    @Override
    @Transactional
    public TokenResponse refresh(String refreshToken) {
        log.info("Token yenilənir:{}", refreshToken);
        String email = cacheService.getContent(REFRESH_TOKEN_HEADER, refreshToken);

        if (email == null)
            throw new InvalidCredentialsException("Kontent mövcud deyil");

        User user = userService.getByEmail(email);

        cacheService.deleteContent(REFRESH_TOKEN_HEADER, refreshToken);

        String accessToken = jwtService.generateAccessToken(email);
        String newRefreshToken = jwtService.generateRefreshToken(email);

        log.info("Token yeniləndi:{}", email);
        return new TokenResponse(accessToken, newRefreshToken, user.getRole(), user.getPack());
    }

    @Override
    public String forgetPassword(String email) {
        log.info("İstifadəçi parolu unudub:{}", email);
        int randomCode = (int) (Math.random() * 9000) + 1000;
        if (!userService.existsByEmail(email))
            throw new ResourceNotFoundException("Email yanlışdır");

        String emailResponse =
                emailService.sendEmail(
                        email, appName + " Verifikasiya kodu", "Kodunuz: " + randomCode);
        cacheService.saveContent(
                FORGET_PASSWORD_HEADER, email, String.valueOf(randomCode), (long) (2 * 60 * 1000));

        return emailResponse;
    }

    @Override
    public String verifyEmailCode(String email, String code) {
        log.info("Verifikasiya olunur:{}", email);
        String redisCode = cacheService.getContent(FORGET_PASSWORD_HEADER, email);

        if (redisCode == null || !redisCode.equals(code))
            throw new BadRequestException("Kod yanlışdır");

        cacheService.deleteContent(FORGET_PASSWORD_HEADER, email);
        UUID uuid = UUID.randomUUID();
        cacheService.saveContent(ACCESS_RESET_PASSWORD, email, uuid.toString(), (long) 10 * 60 * 1000);
        return uuid.toString();
    }

    @Override
    public String resetPassword(ResetPasswordRequest request) {
        log.info("Parol yenilənir:{}", request.email());
        String redisUuid = cacheService.getContent(ACCESS_RESET_PASSWORD, request.email());
        if (redisUuid == null || !redisUuid.equals(request.uuid()))
            return "Please don't try to hack me. I know who you are. My eyes on you.";

        cacheService.deleteContent(ACCESS_RESET_PASSWORD, request.email());
        User user = userService.getByEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        userService.save(user);
        log.info("Istifadəçi parolu yenilədi:{}", request.email());
        return "Parolunuz yeniləndi";
    }

    @Override
    public TokenResponse resetCurrentUserPassword(String oldPassword, String newPassword) {
        log.info("Hazırki istifadəçi parolunu yeniləyir");
        User user = userService.getCurrentUser();
        if (!passwordEncoder.matches(oldPassword, user.getPassword()))
            throw new InvalidCredentialsException("Köhnə parol yanlışdır");

        user.setPassword(passwordEncoder.encode(newPassword));
        userService.save(user);

        String email = user.getEmail();
        log.info("Hazırki istifadəçi parolunu yeniləndi:{}", email);
        return new TokenResponse(jwtService.generateAccessToken(email),
                jwtService.generateRefreshToken(email), user.getRole(), user.getPack());
    }

    @Override
    public TokenResponse finishRegister(Role role, boolean isAcceptedTerms) {
        log.info("Registrasiya sonlandırılır");
        if (!isAcceptedTerms) throw new BadRequestException("İstifadəçi şərtləri qəbul etməyib");

        User currentUser = userService.getCurrentUser();
        if (!Role.EMPTY.equals(currentUser.getRole()))
            throw new BadRequestException("Istifadəçi mövcuddur");

        if (Role.ADMIN.equals(role) || Role.EMPTY.equals(role))
            throw new BadRequestException("Rol boş və ya admin ola bilməz");

        currentUser.setPack(Role.TEACHER.equals(role) ? packService.getPackByName(defaultPackName) : null);

        currentUser.setInfo(
                Role.TEACHER.equals(role) ? new TeacherInfo(Instant.now(), 0, 0, new HashMap<>()) : null);

        currentUser.setRole(role);
        userService.save(currentUser);

        if (Role.TEACHER.equals(role))
            questionStorageRepository.save(QuestionStorage.builder().teacher(currentUser).build());

        String accessToken = jwtService.generateAccessToken(currentUser.getEmail());
        String refreshToken = jwtService.generateRefreshToken(currentUser.getEmail());

        String message = Role.TEACHER.equals(role) ? "Müəllim " : "Tələbə ";
        message = message.concat("qeydiyyatdan keçdi (google): ").concat(currentUser.getEmail());

        log.info(message);
        logService.save(message, currentUser);
        return new TokenResponse(accessToken, refreshToken, currentUser.getRole(), currentUser.getPack());
    }
}
