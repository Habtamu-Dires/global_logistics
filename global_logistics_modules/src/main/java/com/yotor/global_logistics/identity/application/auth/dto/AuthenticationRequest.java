package com.yotor.global_logistics.identity.application.auth.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;

@Builder
public record AuthenticationRequest(
        @NotEmpty(message = "Phone is Mandatory")
        String phone,
        @NotEmpty(message = "Password is Mandatory")
        String password
) { }
