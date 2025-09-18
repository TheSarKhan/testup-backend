package com.exam.examapp.service.interfaces;

import com.exam.examapp.dto.request.ProfileUpdateRequest;
import com.exam.examapp.dto.response.ProfileResponse;
import com.exam.examapp.model.TeacherInfo;
import com.exam.examapp.security.dto.response.TokenResponse;
import org.springframework.web.multipart.MultipartFile;

public interface ProfileService {
  ProfileResponse getProfileInfo();

  ProfileResponse getProfileSettings();

  TeacherInfo getTeacherInfo();

  TokenResponse updateProfileInfo(ProfileUpdateRequest request);

  void updateProfilePicture(MultipartFile profilePicture);
}
