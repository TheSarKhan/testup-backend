package com.exam.examapp.mapper;

import com.exam.examapp.dto.response.ProfileResponse;
import com.exam.examapp.model.User;

public class ProfileMapper {
  public static ProfileResponse userToProfileResponse(User user) {
    return new ProfileResponse(
        user.getId().toString(), user.getFullName(), user.getProfilePictureUrl(), user.getRole());
  }

  public static ProfileResponse userToProfileResponseDetailed(User user) {
    return new ProfileResponse(
        user.getId().toString(),
        user.getFullName(),
        user.getProfilePictureUrl(),
        user.getRole(),
        user.getEmail(),
        user.getPhoneNumber());
  }
}
