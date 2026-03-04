package com.yotor.global_logistics.identity.application.vehicle;

import com.yotor.global_logistics.identity.application.vehicle.dto.CreateVehicleRequest;
import com.yotor.global_logistics.identity.application.vehicle.dto.VehicleProfileView;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vehicles")
@RequiredArgsConstructor
@Tag(name = "vehicles")
public class VehicleController {

    private final VehicleService vehicleService;

    @PostMapping("/create")
    public ResponseEntity<?> createVehicle(
            @RequestBody @Valid CreateVehicleRequest req
            ){
         vehicleService.createVehicle(req);
         return ResponseEntity.accepted().build();
    }

    @PutMapping("/approve/{vehicle-id}")
    public ResponseEntity<?> approveVehicle(
            @PathVariable("vehicle-id") String vehicleId
    ){
        vehicleService.approveVehicle(vehicleId);
        return ResponseEntity.accepted().build();
    }

    @PutMapping("/suspend/{vehicle-id}")
    public ResponseEntity<?> suspendVehicle(
            @PathVariable("vehicle-id") String vehicleId
    ){
        vehicleService.suspendVehicle(vehicleId);
        return ResponseEntity.accepted().build();
    }

    @PutMapping("/reject/{vehicle-id}")
    public ResponseEntity<?> rejectVehicle(
            @PathVariable("vehicle-id") String vehicleId
    ){
        vehicleService.rejectVehicle(vehicleId);
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/profile/{driver-id}")
    public ResponseEntity<List<VehicleProfileView>> getVehicleByDriver(
            @PathVariable("driver-id") String driverId
    ){
       var  res = vehicleService.getVehicleByDriver(driverId);
       return ResponseEntity.ok(res);
    }

    @GetMapping
    public ResponseEntity<List<VehicleProfileView>> getAllVehicles(){
        var  res = vehicleService.getAllVehicles();
        return ResponseEntity.ok(res);
    }
}
