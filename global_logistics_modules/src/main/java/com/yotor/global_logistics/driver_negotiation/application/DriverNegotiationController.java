package com.yotor.global_logistics.driver_negotiation.application;

import com.yotor.global_logistics.driver_negotiation.application.dto.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/driver-negotiations")
@Tag(name = "driver-negotiations")
@RequiredArgsConstructor
public class DriverNegotiationController {

    private final DriverNegotiationService negotiationService;


    @PostMapping("/send-initial-offers")
    public ResponseEntity<?> sendInitialDriverOffers(
            @RequestBody @Valid AdminInitialDriverOfferRequest req
    ){
        negotiationService.adminSendInitialDriverOffers(req);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/admin-counter")
    public ResponseEntity<?> adminCounter(
            @RequestBody @Valid AdminCounterRequest req
    ){
        negotiationService.adminCounter(req);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/dirver-counter")
    public ResponseEntity<?> driverCounter(
            @RequestBody @Valid DriverCounterRequest req
    ){
        negotiationService.driverCounter(req);
        return ResponseEntity.accepted().build();
    }

    @PutMapping("/driver-accepts")
    public ResponseEntity<?> driverAccepts(
            @RequestBody @Valid DriverAcceptRequest req
    ){
        negotiationService.driverAcceptOffer(req);
        return ResponseEntity.accepted().build();
    }

    @PutMapping("/driver-rejects")
    public ResponseEntity<?> driverRejects(
        @RequestBody @Valid RejectOrCancelRequest req
    ){
        negotiationService.driverRejectOffer(req);
        return ResponseEntity.accepted().build();
    }

    @PutMapping("/driver-cancel")
    public ResponseEntity<?> driverCancel(
        @RequestBody @Valid RejectOrCancelRequest req
    ){
        negotiationService.driverCancel(req);
        return ResponseEntity.accepted().build();
    }

    @PutMapping("/admin-cancel")
    public ResponseEntity<?> adminCancel(
        @RequestBody @Valid RejectOrCancelRequest req
    ){
        negotiationService.adminCancel(req);
        return ResponseEntity.accepted().build();
    }

    /** getters **/
    @GetMapping("/driver-offers")
    public ResponseEntity<List<DriverOfferView>> getDriverOffers(){
        var res = negotiationService.getActiveDriverOffers();
        return ResponseEntity.ok(res);
    }

    @GetMapping("/{shipment-id}")
    public ResponseEntity<List<DriverNegotiationResponse>> getDriverNegotiations(
            @PathVariable("shipment-id") UUID shipmentId
    ){
        var res = negotiationService.getDriverNegotiations(shipmentId);
        return ResponseEntity.ok(res);
    }

}
