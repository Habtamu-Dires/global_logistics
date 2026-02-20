package com.yotor.global_logistics.identity.auth.dto;

public record LogoutRequest(
        String refreshToken
) {
}
