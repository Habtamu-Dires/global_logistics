package com.yotor.global_logistics.identity.application.identity.dto;

import com.yotor.global_logistics.identity.domain.user.enums.UserRole;
import com.yotor.global_logistics.identity.domain.user.enums.UserStatus;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record RegisteredUsers(
        UUID publicId,
        String firstName,
        String lastName,
        String phone,
        Set<UserRole> roles,
        UserStatus status,
        Instant createdAt
) {
}
