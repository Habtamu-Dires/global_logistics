package com.yotor.global_logestics.identity.api.dto;

import com.yotor.global_logestics.identity.domain.user.UserIdentity;
import com.yotor.global_logestics.identity.domain.user.enums.UserRole;
import com.yotor.global_logestics.identity.domain.user.enums.UserStatus;
import lombok.Builder;

import java.util.UUID;


@Builder
public record UserSummary(
        UUID externalId,
        String phone,
        UserRole role,
        UserStatus status
) {

    public static UserSummary from(UserIdentity userIdentity){
        return UserSummary.builder()
                .externalId(userIdentity.getExternalId())
                .phone(userIdentity.getPhone())
                .role(userIdentity.getRole())
                .status(userIdentity.getStatus())
                .build();
    }
}
