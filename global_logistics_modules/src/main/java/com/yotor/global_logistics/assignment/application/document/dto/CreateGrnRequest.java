package com.yotor.global_logistics.assignment.application.document.dto;

public record CreateGrnRequest(
        Integer receivedQuantity,
        String receivedWeight,
        String receivedVolume,
        Integer damageQuantity,
        Integer shortageQuantity,
        String conditionNote
) {
}
