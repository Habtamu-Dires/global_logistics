package com.yotor.global_logistics.identity.auth.dto;

import lombok.*;


@Builder
public record RefreshRequest(
    String refreshToken
) { }
