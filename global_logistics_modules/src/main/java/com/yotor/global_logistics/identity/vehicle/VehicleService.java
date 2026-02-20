package com.yotor.global_logistics.identity.vehicle;

import com.yotor.global_logistics.exception.BusinessException;
import com.yotor.global_logistics.exception.ErrorCode;
import com.yotor.global_logistics.identity.domain.user.DriverProfile;
import com.yotor.global_logistics.identity.domain.vehicle.Vehicle;
import com.yotor.global_logistics.identity.persistence.DriverProfileRepository;
import com.yotor.global_logistics.identity.persistence.VehicleRepository;
import com.yotor.global_logistics.identity.vehicle.dto.CreateVehicleRequest;
import com.yotor.global_logistics.identity.vehicle.dto.VehicleProfileView;
import com.yotor.global_logistics.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepo;
    private final DriverProfileRepository driverRepo;

    @PreAuthorize("hasRole('DRIVER')")
    public void createVehicle(CreateVehicleRequest req){
        var driverExternalId = SecurityUtils.currentUser().userPublicId();
        DriverProfile driver = driverRepo.findByPublicId(driverExternalId)
                .orElseThrow(()-> new BusinessException(ErrorCode.DRIVER_NOT_FOUND));

        Vehicle vehicle = Vehicle.create(
                driver.getId(),
                req.plateNumber(),
                req.type(),
                req.insuranceDoc(),
                req.details(),
                req.photo()
        );

        vehicleRepo.save(vehicle);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void approveVehicle(String vehicleId){
        UUID vehicleExternalId = UUID.fromString(vehicleId);
        Vehicle vehicle = vehicleRepo.findByPublicId(vehicleExternalId)
                .orElseThrow(() -> new BusinessException(ErrorCode.VEHICLE_NOT_FOUND));

        vehicle.approve();
        vehicleRepo.save(vehicle);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void suspendVehicle(String vehicleId){
        UUID vehicleExternalId = UUID.fromString(vehicleId);
        Vehicle vehicle = vehicleRepo.findByPublicId(vehicleExternalId)
                .orElseThrow(() -> new BusinessException(ErrorCode.VEHICLE_NOT_FOUND));

        vehicle.suspend();
        vehicleRepo.save(vehicle);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void rejectVehicle(String vehicleId){
        UUID vehicleExternalId = UUID.fromString(vehicleId);
        Vehicle vehicle = vehicleRepo.findByPublicId(vehicleExternalId)
                .orElseThrow(() -> new BusinessException(ErrorCode.VEHICLE_NOT_FOUND));

        vehicle.reject();
        vehicleRepo.save(vehicle);
    }

    @PreAuthorize("hasAnyRole('ADMIN','DRIVER')")
    public List<VehicleProfileView> getVehicleByDriver(String driverId) {
        UUID driverExternalId = UUID.fromString(driverId);
        return vehicleRepo.findViewByDriverId(driverExternalId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<VehicleProfileView> getAllVehicles(){
        return vehicleRepo.findAllVehicles();
    }
}
