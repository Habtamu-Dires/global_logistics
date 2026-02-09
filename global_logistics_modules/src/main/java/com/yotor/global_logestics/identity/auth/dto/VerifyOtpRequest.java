package com.yotor.global_logestics.identity.auth.dto;


public record VerifyOtpRequest(
        String phone,
        String code
) {
}
