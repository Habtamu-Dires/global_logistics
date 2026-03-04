package com.yotor.global_logistics.identity.application.auth.dto;

import com.yotor.global_logistics.identity.domain.user.enums.UserRole;

public record RegisterRequest(
        String firstName,
        String lastName,
        String phoneNumber,
        UserRole role,
        String password,
        String confirmPassword
) {
}
