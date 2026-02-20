package com.yotor.global_logistics.security;


import java.util.UUID;

public record AuthenticatedUser(
        UUID userPublicId,
        String role
) {}
