package com.yotor.global_logestics.shipment.application;

import com.yotor.global_logestics.shipment.application.dto.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/shipments")
@RequiredArgsConstructor
@Tag(name = "shipments")
public class ShipmentController {

    private final ShipmentService shipmentService;

    @PostMapping("/create")
    public ResponseEntity<UUID> createShipment(
            @RequestBody @Valid CreateShipmentRequest req
    ){
        var shipmentId = shipmentService.createShipment(req);
        return ResponseEntity.ok(shipmentId);
    }

    @PostMapping("/admin-request-change")
    public ResponseEntity<?> adminRequestChange(
            @RequestBody @Valid AdminRequestChange req
    ){
      shipmentService.adminRequestsChange(req);
      return ResponseEntity.accepted().build();
    }

    @PostMapping("/consignor-counter-offer")
    public ResponseEntity<?> consignorCounter(
            @RequestBody @Valid ConsignorCounterReq req
    ){
        shipmentService.consignorCounter(req);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/consignor-accept-offer")
    public ResponseEntity<?> consignorAccepts(
            @RequestBody @Valid ConsignorRequest req
    ){
        shipmentService.consignorAccepts(req);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/consignor-reject-offer")
    public ResponseEntity<?> consignorRejects(
        @RequestBody @Valid ConsignorRequest req
    ){
        shipmentService.consignorRejects(req);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/consignor-cancel")
    public ResponseEntity<?> consignorCancel(
            @RequestBody @Valid ConsignorRequest req
    ){
        shipmentService.consignorCancel(req);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/admin-approve")
    public ResponseEntity<?> adminApprove(
            @RequestBody @Valid AdminRequest req
            ){
        shipmentService.adminApprove(req);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/admin-rejects")
    public ResponseEntity<?> adminRejects(
            @RequestBody @Valid AdminRequest req
    ){
        shipmentService.adminRejects(req);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/admin-cancel")
    public ResponseEntity<?> adminCancel(
            @RequestBody @Valid AdminRequest req
    ){
        shipmentService.adminCancel(req);
        return ResponseEntity.accepted().build();
    }

    /** admin get requests  */
    @GetMapping
    public ResponseEntity<List<ShipmentResponse>> getAllShipments(){
        return ResponseEntity.ok(shipmentService.getAllShipments());
    }


    /**  consignor get requests */
    //get consignor shipments
    @GetMapping("/consignor")
    public ResponseEntity<List<ShipmentResponse>> getConsignorShipments(){
        return ResponseEntity.ok(shipmentService.getConsignorShipments());
    }

}
