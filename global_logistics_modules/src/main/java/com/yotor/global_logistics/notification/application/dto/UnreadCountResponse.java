package com.yotor.global_logistics.notification.application.dto;

import lombok.Builder;

@Builder
public record UnreadCountResponse(

) {
    public static UnreadCountResponse from(UnreadCountProjection projection) {
        return new UnreadCountResponse();
    }
}
