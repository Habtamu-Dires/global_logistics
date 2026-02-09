package com.yotor.global_logestics.shipment.application;

import com.yotor.global_logestics.exception.BusinessException;
import com.yotor.global_logestics.exception.ErrorCode;
import com.yotor.global_logestics.security.SecurityUtils;
import com.yotor.global_logestics.shipment.application.dto.ShipmentStatusHistoryDto;
import com.yotor.global_logestics.shipment.persistence.ShipmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ShipmentStatusHistoryService {

    private final ShipmentRepository shipmentRepo;

    @PreAuthorize("hasAnyRole('ADMIN','CONSIGNOR')")
    public List<ShipmentStatusHistoryDto> getStatusHistory(UUID shipmentExternalId){

        UUID userId = SecurityUtils.currentUser().userExternalId();
        String role = SecurityUtils.currentUser().role();

        var shipmentIds  = shipmentRepo.findIdAndConsignorIdByExternalId(shipmentExternalId)
                .orElseThrow(()-> new BusinessException(ErrorCode.SHIPMENT_NOT_FOUND));

        if(role.equals("CONSIGNOR") && !shipmentIds.consignorId().equals(userId)){
            throw new BusinessException(ErrorCode.USER_NOT_ALLOWED);
        }

        return shipmentRepo.findStatusHistory(shipmentIds.id());
    }
}
