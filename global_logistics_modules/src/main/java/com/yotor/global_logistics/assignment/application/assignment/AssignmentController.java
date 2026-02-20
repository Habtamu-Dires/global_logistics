package com.yotor.global_logistics.assignment.application.assignment;

import com.yotor.global_logistics.assignment.application.assignment.dto.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/assignments")
@Tag(name = "assignments")
@RequiredArgsConstructor
public class AssignmentController {

    private final AssignmentService assignmentService;

    @PostMapping("/select-driver")
    public ResponseEntity<IdResponse> selectDriver(
            @RequestBody @Valid AdminDriverSelectRequest req
    ){
        var res = assignmentService.selectDriver(req);
        return ResponseEntity.ok(res);
    }

    @PutMapping("/confirm-loading")
    public ResponseEntity<?> confirmLoading(
            @RequestBody @Valid AssignmentRequest req
    ){
        assignmentService.confirmLoading(req);
        return ResponseEntity.accepted().build();
    }

    @PutMapping("/start-trasnsport")
    public ResponseEntity<?> startTransport(
            @RequestBody @Valid AssignmentRequest req
    ){
        assignmentService.startTransport(req);
        return ResponseEntity.accepted().build();
    }

    @PutMapping("/driver-complete")
    public ResponseEntity<?> confirmOffloading(
            @RequestBody @Valid AssignmentRequest req
    ){
        assignmentService.confirmOffloading(req);
        return ResponseEntity.accepted().build();
    }

    @PutMapping("/confirm-receipt-by-consignor")
    public ResponseEntity<?> confirmReceiptByConsignor(
            @RequestBody @Valid AssignmentRequest req
    ){
        assignmentService.confirmReceiptByConsignor(req);
        return ResponseEntity.accepted().build();
    }

    @PutMapping("/cancel")
    public ResponseEntity<?> driverCancel(
            @RequestBody @Valid AssignmentRequest req
    ){
        assignmentService.cancelAssignment(req);
        return ResponseEntity.accepted().build();
    }

    /** --- get requests --- **/

    /** AdMIN get requests ***/
    // get all assignments by shipment id
    @GetMapping("/{shipment-id}")
    public ResponseEntity<List<AssignmentAdminView>> getAssignmentsByShipmentId(
            @PathVariable("shipment-id") UUID shipmentId
    ){
        var res = assignmentService.getAssignmentByShipmentId(shipmentId);
        return ResponseEntity.ok(res);
    }

    /** Driver get requests ***/

    @GetMapping("/driver")
    public ResponseEntity<List<AssignmentDriverView>> getAssignmentsOfDriver(){
        var res = assignmentService.getAssignmentsOfDriver();
        return ResponseEntity.ok(res);
    }

}
