package com.yotor.global_logistics.notification.application.dto;

public record UnreadCountProjection(
        String getReferenceType,
        long getTotal
) {
}
