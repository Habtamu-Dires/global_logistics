package com.yotor.global_logistics.identity.application.dto;

import com.yotor.global_logistics.identity.domain.user.UserIdentity;
import com.yotor.global_logistics.identity.domain.user.enums.UserRole;
import com.yotor.global_logistics.identity.domain.user.enums.UserStatus;
import lombok.Builder;

import java.util.UUID;


@Builder
public record UserSummary(
        UUID publicId,
        String phone,
        String fullName,
        UserRole role,
        UserStatus status
) {

    public static UserSummary from(UserIdentity userIdentity){
        return UserSummary.builder()
                .publicId(userIdentity.getPublicId())
                .phone(userIdentity.getPhone())
                .fullName(userIdentity.getFirstName() + " " + userIdentity.getLastName())
                .role(userIdentity.getRole())
                .status(userIdentity.getStatus())
                .build();
    }
}
