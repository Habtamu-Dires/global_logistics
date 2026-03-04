package com.yotor.global_logistics.identity.application.auth.dto;

import jakarta.validation.constraints.NotEmpty;

public record ResetPasswordRequest(
        @NotEmpty(message = "Phone is Mandatory")
        String phone,
        @NotEmpty(message = "OTP Code is Mandatory")
        String otpCode,
        @NotEmpty(message = "New Password is Mandatory")
        String newPassword,
        @NotEmpty(message = "Confirm Password is Mandatory")
        String confirmPassword
) {
}
