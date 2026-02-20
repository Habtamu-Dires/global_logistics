package com.yotor.global_logistics.identity.auth.dto;

public record AuthTokens(
        String accessToken,
        String refreshToken
) {
}
