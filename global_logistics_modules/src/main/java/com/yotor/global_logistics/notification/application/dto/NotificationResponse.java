package com.yotor.global_logistics.notification.application.dto;

import com.yotor.global_logistics.notification.domain.Notification;
import lombok.Builder;

@Builder
public record NotificationResponse(

) {
    public static NotificationResponse from(Notification notification) {
        return NotificationResponse.builder().build();
    }
}
