package com.yotor.global_logistics.assignment.application.document.dto;

public record CreateGdnRequest(
        String consigneeName,
        String consigneeContact,
        Integer quantity,
        String weight,
        String volume,
        String packagingType,
        String remarks
) {}
