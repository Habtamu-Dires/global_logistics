package com.yotor.global_logistics.identity.application.auth.dto;

import jakarta.validation.constraints.NotEmpty;

public record ForgetPasswordRequest(

        @NotEmpty(message = "Phone is Mandatory")
        String phone
) {}
