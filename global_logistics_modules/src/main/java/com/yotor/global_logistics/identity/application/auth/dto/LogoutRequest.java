package com.yotor.global_logistics.identity.application.auth.dto;

public record LogoutRequest(
        String refreshToken
) {
}
