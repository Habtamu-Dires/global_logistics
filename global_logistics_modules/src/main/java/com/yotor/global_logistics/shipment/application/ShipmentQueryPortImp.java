package com.yotor.global_logistics.shipment.application;

import com.yotor.global_logistics.exception.BusinessException;
import com.yotor.global_logistics.exception.ErrorCode;
import com.yotor.global_logistics.shipment.api.ShipmentQueryPort;
import com.yotor.global_logistics.shipment.application.dto.ShipmentResponse;
import com.yotor.global_logistics.shipment.domain.Shipment;
import com.yotor.global_logistics.shipment.domain.dto.ShipmentStatus;
import com.yotor.global_logistics.shipment.persistence.ShipmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ShipmentQueryPortImp implements ShipmentQueryPort {

    private final ShipmentRepository shipmentRepo;


    @Override
    public ShipmentResponse getShipmentDetails(UUID shipmentId){
        Shipment shipment = shipmentRepo.findByPublicId(shipmentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SHIPMENT_NOT_FOUND));

        return ShipmentResponse.from(shipment);
    }

    @Override
    public int getRequiredVehicleNumber(UUID shipmentId) {
        return shipmentRepo.findRequiredVehicleNumber(shipmentId);
    }

    @Override
    public boolean isShipmentOpenToDriverAssignment(UUID shipmentId){
        String currentStatus = shipmentRepo.findShipmentStatus(shipmentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SHIPMENT_NOT_FOUND));

        return currentStatus.equals(ShipmentStatus.ADMIN_APPROVED.toString())
                || currentStatus.equals(ShipmentStatus.DRIVER_ASSIGNED.toString());
    }

    @Override
    public void markDriverAssigned(UUID shipmentId, UUID adminId){
        Shipment shipment = shipmentRepo.findByPublicId(shipmentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SHIPMENT_NOT_FOUND));

        shipment.markDriverAssigned(adminId);
        shipmentRepo.save(shipment);
    }

    @Override
    public void markInProgress(UUID shipmentId, UUID adminId){
        Shipment shipment = shipmentRepo.findByPublicId(shipmentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SHIPMENT_NOT_FOUND));

        shipment.markInProgress(adminId);
        shipmentRepo.save(shipment);
    }

    @Override
    public void markCompleted(UUID shipmentId, UUID adminId){
        Shipment shipment = shipmentRepo.findByPublicId(shipmentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SHIPMENT_NOT_FOUND));

        shipment.markCompleted(adminId);
        shipmentRepo.save(shipment);
    }

    @Override
    public UUID getConsignorId(UUID shipmentId){
        return shipmentRepo.findConsignorId(shipmentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CONSIGNOR_NOT_FOUND));
    }

}
