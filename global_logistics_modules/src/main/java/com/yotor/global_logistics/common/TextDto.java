package com.yotor.global_logistics.common;


import jakarta.validation.constraints.NotEmpty;

public record TextDto(
        @NotEmpty(message = "Text is required")
        String text
) {}
