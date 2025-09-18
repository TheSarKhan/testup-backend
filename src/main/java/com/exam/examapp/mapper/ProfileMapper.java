package com.exam.examapp.mapper;

import com.exam.examapp.dto.response.ProfileInfoResponse;
import com.exam.examapp.dto.response.ProfileSettingsResponse;
import com.exam.examapp.model.User;

public class ProfileMapper {
    public static ProfileInfoResponse toInfoResponse(User user) {
        return new ProfileInfoResponse(user.getId().toString(), user.getFullName(), user.getProfilePictureUrl(), user.getRole());
    }

    public static ProfileSettingsResponse toSettingsResponse(User user) {
        return new ProfileSettingsResponse(
                user.getId().toString(),
                user.getFullName(),
                user.getProfilePictureUrl(),
                user.getRole(),
                user.getEmail(),
                user.getPhoneNumber());
    }
}
