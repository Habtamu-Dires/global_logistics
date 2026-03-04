package com.yotor.global_logistics.identity.application.auth.dto;

import jakarta.validation.constraints.NotEmpty;

public record ChangePasswordRequest(
        @NotEmpty(message = "Phone is Mandatory")
        String phone,
        @NotEmpty(message = "Current Password is Mandatory")
        String currentPassword,
        @NotEmpty(message = "New Password is Mandatory")
        String newPassword,
        @NotEmpty(message = "Confirm Password is Mandatory")
        String confirmPassword
) {
}
