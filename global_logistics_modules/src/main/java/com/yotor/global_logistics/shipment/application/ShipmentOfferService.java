package com.yotor.global_logistics.shipment.application;

import com.yotor.global_logistics.exception.BusinessException;
import com.yotor.global_logistics.exception.ErrorCode;
import com.yotor.global_logistics.security.SecurityUtils;
import com.yotor.global_logistics.shipment.application.dto.ShipmentOfferDto;
import com.yotor.global_logistics.shipment.persistence.ShipmentRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/shipment-offers")
@RequiredArgsConstructor
@Tag(name = "shipment-offers")
public class ShipmentOfferService {

    private final ShipmentRepository shipmentRepo;

    @PreAuthorize("hasAnyRole('ADMIN','CONSIGNOR')")
    public List<ShipmentOfferDto> getShipmentOffers(UUID shipmentExternalId){
        UUID userId = SecurityUtils.currentUser().userPublicId();
        String role = SecurityUtils.currentUser().role();

        var shipmentIds = shipmentRepo.findIdAndConsignorIdByPublicId(shipmentExternalId)
                .orElseThrow(()-> new BusinessException(ErrorCode.SHIPMENT_NOT_FOUND));

        if(role.equals("CONSIGNOR") && !shipmentIds.consignorId().equals(userId)){
            throw new BusinessException(ErrorCode.USER_NOT_ALLOWED);
        }

        return shipmentRepo.getShipmentOffers(shipmentIds.id());
    }
}
