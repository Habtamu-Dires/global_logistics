package com.yotor.global_logistics.assignment.application.assignment.dto;

import java.util.UUID;

public record AdminDriverSelectRequest(
        UUID negotiationId,
        String remark
) {
}
