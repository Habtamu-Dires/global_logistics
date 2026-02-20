package com.yotor.global_logistics.shipment.application;


import com.yotor.global_logistics.shipment.application.dto.ShipmentOfferDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/shipment-offers")
@RequiredArgsConstructor
public class ShipmentOfferController {

    private final ShipmentOfferService shipmentOfferService;

    @GetMapping("/{shipment-id}")
    public ResponseEntity<List<ShipmentOfferDto>> getShipmentOffers(
           @PathVariable("shipment-id") UUID shipmentId
    ){
        var shipmentOffers = shipmentOfferService.getShipmentOffers(shipmentId);
        return ResponseEntity.ok(shipmentOffers);
    }
}
