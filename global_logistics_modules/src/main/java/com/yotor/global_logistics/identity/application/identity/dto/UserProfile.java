package com.yotor.global_logistics.identity.application.identity.dto;

import com.yotor.global_logistics.identity.domain.user.UserIdentity;
import com.yotor.global_logistics.identity.domain.user.enums.UserRole;
import com.yotor.global_logistics.identity.domain.user.enums.UserStatus;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;


@Builder
public record UserProfile(
        @NotNull(message = "Public id is required")
        UUID publicId,
        String nationalId,
        @NotEmpty(message = "Public id is required")
        String phone,
        @NotEmpty(message = "Public id is required")
        String firstName,
        @NotEmpty(message = "Public id is required")
        String lastName,
        String profilePic,
        @NotNull(message = "Roles are required")
        Set<UserRole> roles,
        @NotNull(message = "Status is required")
        UserStatus status,
        Boolean phoneVerified,
        String remark,
        Instant createdAt,
        Instant updatedAt
) {

    public static UserProfile from(UserIdentity userIdentity){
        return UserProfile.builder()
                .publicId(userIdentity.getPublicId())
                .nationalId(userIdentity.getNationalId())
                .phone(userIdentity.getPhone())
                .firstName(userIdentity.getFirstName())
                .lastName(userIdentity.getLastName())
                .profilePic(userIdentity.getProfilePic())
                .roles(userIdentity.getRoles())
                .status(userIdentity.getStatus())
                .phoneVerified(userIdentity.isPhoneVerified())
                .remark(userIdentity.getRemark())
                .createdAt(userIdentity.getCreatedAt())
                .updatedAt(userIdentity.getUpdatedAt())
                .build();
    }
}
