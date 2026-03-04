package com.yotor.global_logistics.identity.application.auth.dto;


import jakarta.validation.constraints.NotEmpty;

public record VerifyOtpRequest(
        @NotEmpty(message = "Phone is Mandatory")
        String phone,
        @NotEmpty(message = "Code is Mandatory")
        String code
) {
}
