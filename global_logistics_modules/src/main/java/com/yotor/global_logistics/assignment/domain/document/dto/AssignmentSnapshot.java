package com.yotor.global_logistics.assignment.domain.document.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record AssignmentSnapshot(
        String consignorName,
        String consignorPhone,
        String driverName,
        String driverPhone,
        String vehicleType,
        String vehiclePlateNo,
        String goodsType,
        String goodsDescription,
        String loadingLocation,
        LocalDateTime loadingDate,
        String offloadingLocation
) {

}

