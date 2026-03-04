package com.yotor.global_logistics.identity.application.consignor.dto;

public record CreateConsignorProfileRequest(
        String businessName,
        String tradeLicence
) {
}
