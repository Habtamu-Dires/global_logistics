package com.yotor.global_logistics.identity.application.auth.dto;

import lombok.*;


@Builder
public record RefreshRequest(
    String refreshToken
) { }
