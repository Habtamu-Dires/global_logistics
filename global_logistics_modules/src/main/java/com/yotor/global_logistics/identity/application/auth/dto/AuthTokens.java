package com.yotor.global_logistics.identity.application.auth.dto;

public record AuthTokens(
        String accessToken,
        String refreshToken,
        boolean isTempPassword
) {
}
