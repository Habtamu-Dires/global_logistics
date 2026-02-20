package com.yotor.global_logistics.identity.auth.dto;

import java.util.UUID;

public record RegisterResponse(
   UUID userId,
   String phone
) {}
