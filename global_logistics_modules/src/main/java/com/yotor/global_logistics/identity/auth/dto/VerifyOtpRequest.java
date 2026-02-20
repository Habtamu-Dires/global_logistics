package com.yotor.global_logistics.identity.auth.dto;


public record VerifyOtpRequest(
        String phone,
        String code
) {
}
