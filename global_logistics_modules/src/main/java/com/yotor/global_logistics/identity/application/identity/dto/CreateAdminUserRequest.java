package com.yotor.global_logistics.identity.application.identity.dto;


public record CreateAdminUserRequest(
        String phone,
        String firstName,
        String lastName,
        String remark
) {}
