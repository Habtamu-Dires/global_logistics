package com.yotor.global_logestics.security;


import java.util.UUID;

public record AuthenticatedUser(
        UUID userExternalId,
        String role
) {}
