package com.exam.examapp.dto.response;

import com.exam.examapp.model.enums.Role;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ProfileResponse(String id,
                              String username,
                              String profilePictureUrl,
                              Role role,
                              String email,
                              String phoneNumber) {

    public ProfileResponse(String id, String username, String profilePictureUrl,
                           Role role, String email, String phoneNumber) {
        this.id = id;
        this.username = username;
        this.profilePictureUrl = profilePictureUrl;
        this.role = role;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public ProfileResponse(String id, String username, String profilePictureUrl, Role role) {
        this(id, username, profilePictureUrl, role, null, null);
    }
}
