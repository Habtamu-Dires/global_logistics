package com.yotor.global_logistics.driver_negotiation.application.dto;

import java.util.UUID;

public record RejectOrCancelRequest(
        UUID negotiationId,
        String reason
) {
}
