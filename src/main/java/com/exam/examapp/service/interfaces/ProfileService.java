package com.exam.examapp.service.interfaces;

import com.exam.examapp.dto.request.ProfileUpdateRequest;
import com.exam.examapp.dto.response.ProfileInfoResponse;
import com.exam.examapp.dto.response.ProfileSettingsResponse;
import com.exam.examapp.model.TeacherInfo;
import com.exam.examapp.security.dto.response.TokenResponse;
import org.springframework.web.multipart.MultipartFile;

public interface ProfileService {
    ProfileInfoResponse getProfileInfo();

    ProfileSettingsResponse getProfileSettings();

    TeacherInfo getTeacherInfo();

    TokenResponse updateProfileInfo(ProfileUpdateRequest request);

    void updateProfilePicture(MultipartFile profilePicture);
}
