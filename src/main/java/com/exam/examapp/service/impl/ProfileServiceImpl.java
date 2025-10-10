package com.exam.examapp.service.impl;

import com.exam.examapp.dto.request.ProfileUpdateRequest;
import com.exam.examapp.dto.response.ProfileInfoResponse;
import com.exam.examapp.dto.response.ProfileSettingsResponse;
import com.exam.examapp.dto.response.TeacherInfoResponse;
import com.exam.examapp.exception.custom.BadRequestException;
import com.exam.examapp.mapper.ProfileMapper;
import com.exam.examapp.model.User;
import com.exam.examapp.security.dto.response.TokenResponse;
import com.exam.examapp.security.service.impl.JwtService;
import com.exam.examapp.service.interfaces.FileService;
import com.exam.examapp.service.interfaces.LogService;
import com.exam.examapp.service.interfaces.ProfileService;
import com.exam.examapp.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {
    private static final String IMAGE_PATH = "uploads/images/profile_pictures";

    private final UserService userService;

    private final JwtService jwtService;

    private final FileService fileService;

    private final LogService logService;

    @Override
    public ProfileInfoResponse getProfileInfo() {
        return ProfileMapper.toInfoResponse(userService.getCurrentUser());
    }

    @Override
    public ProfileSettingsResponse getProfileSettings() {
        return ProfileMapper.toSettingsResponse(userService.getCurrentUser());
    }

    @Override
    public TeacherInfoResponse getTeacherInfo() {
        return new TeacherInfoResponse(
                userService.getTeacherInfo(),
                userService.getCurrentUser().getPack());
    }

    @Override
    public TokenResponse updateProfileInfo(ProfileUpdateRequest request) {
        log.info("Profil yenilənir");
        User user = userService.getCurrentUser();
        user.setFullName(request.fullName());
        user.setPhoneNumber(request.phoneNumber());

        String email = request.email();
        if (userService.existsByEmail(email) &&
                !userService.getByEmail(email).getId().equals(user.getId()))
            throw new BadRequestException("E-poçt artıq mövcuddur");

        user.setEmail(email);

        userService.save(user);

        User newUser = userService.getByEmail(email);

        TokenResponse tokenResponse = new TokenResponse(
                jwtService.generateAccessToken(email),
                jwtService.generateRefreshToken(email),
                newUser.getRole(),
                newUser.getPack());
        log.info("Profil yeniləndi");
        logService.save("Profil yeniləndi", userService.getCurrentUserOrNull());
        return tokenResponse;
    }

    @Override
    public void updateProfilePicture(MultipartFile profilePicture) {
        log.info("Profil şəkil yenilənir");
        User user = userService.getCurrentUser();

        if (user.getProfilePictureUrl() != null) {
            fileService.deleteFile(IMAGE_PATH, user.getProfilePictureUrl());
            user.setProfilePictureUrl(null);
        }

        if (profilePicture != null) {
            String url = fileService.uploadFile(IMAGE_PATH, profilePicture);
            user.setProfilePictureUrl(url);
        }

        userService.save(user);
        log.info("Profil şəkil yeniləndi");
        logService.save("Profil şəkil yeniləndi", userService.getCurrentUserOrNull());
    }
}
