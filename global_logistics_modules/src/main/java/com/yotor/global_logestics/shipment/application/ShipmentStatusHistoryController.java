package com.yotor.global_logestics.shipment.application;

import com.yotor.global_logestics.shipment.application.dto.ShipmentStatusHistoryDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/shipment-status-history")
@Tag(name = "shipment-status-history")
@RequiredArgsConstructor
public class ShipmentStatusHistoryController {

    private final ShipmentStatusHistoryService shipmentStatusHistoryService;

    @GetMapping("/{shipment-id}")
    public ResponseEntity<List<ShipmentStatusHistoryDto>> getShipmentStatusHistory(
            @PathVariable("shipment-id") UUID shipmentId
    ){
        var statusHistoryList = shipmentStatusHistoryService.getStatusHistory(shipmentId);
        return ResponseEntity.ok(statusHistoryList);
    }
}
