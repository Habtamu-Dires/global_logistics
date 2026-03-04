package com.yotor.global_logistics.identity.event;


public record OtpEvent(
        String phone,
        String code,
        Long expiresInMinutes
) {
}
