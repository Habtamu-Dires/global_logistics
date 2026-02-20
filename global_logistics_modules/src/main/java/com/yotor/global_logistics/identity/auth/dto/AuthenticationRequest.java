package com.yotor.global_logistics.identity.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;



@Builder
public record AuthenticationRequest(
        @NotBlank(message = "VALIDATION.AUTHENTICATION.EMAIL.NOT_BLANK")
        String phone,
        @NotBlank(message = "VALIDATION.AUTHENTICATION.PASSWORD.NOT_BLANK")
        String password
) { }
