package com.yotor.global_logestics.identity.auth.dto;

public record AuthTokens(
        String accessToken,
        String refreshToken
) {
}
