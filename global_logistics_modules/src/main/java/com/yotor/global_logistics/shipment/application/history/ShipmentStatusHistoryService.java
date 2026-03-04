package com.yotor.global_logistics.shipment.application.history;

import com.yotor.global_logistics.exception.BusinessException;
import com.yotor.global_logistics.exception.ErrorCode;
import com.yotor.global_logistics.security.SecurityUtils;
import com.yotor.global_logistics.shipment.application.history.dto.ShipmentStatusHistoryDto;
import com.yotor.global_logistics.shipment.persistence.ShipmentRepository;
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

        UUID userId = SecurityUtils.currentUser().userPublicId();
        String role = SecurityUtils.currentUser().roles().stream().findFirst().orElse("");

        var shipmentIds  = shipmentRepo.findIdAndConsignorIdByPublicId(shipmentExternalId)
                .orElseThrow(()-> new BusinessException(ErrorCode.SHIPMENT_NOT_FOUND));

        if(role.equals("CONSIGNOR") && !shipmentIds.consignorId().equals(userId)){
            throw new BusinessException(ErrorCode.USER_NOT_ALLOWED);
        }

        return shipmentRepo.findStatusHistory(shipmentIds.id());
    }
}
