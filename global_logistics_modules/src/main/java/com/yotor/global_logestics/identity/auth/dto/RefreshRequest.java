package com.yotor.global_logestics.identity.auth.dto;

import lombok.*;


@Builder
public record RefreshRequest(
    String refreshToken
) { }
