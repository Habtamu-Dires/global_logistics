package com.yotor.global_logistics.identity.persistence;

import com.yotor.global_logistics.identity.application.dto.VehicleSummery;
import com.yotor.global_logistics.identity.domain.vehicle.Vehicle;
import com.yotor.global_logistics.identity.vehicle.dto.VehicleProfileView;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VehicleRepository
        extends CrudRepository<Vehicle, Long> {

    @Query("""
            SELECT * FROM vehicle v
            WHERE v.public_id = :publicId
            """)
    Optional<Vehicle> findByPublicId(UUID publicId);

    @Query("""
            SELECT v.plate_number, v.type FROM vehicle v
            JOIN driver_profile dp ON v.driver_id = dp.user_id
            """)
    Optional<VehicleSummery> findVehicleSummeryByDriverId(Long driverId);

    @Query("""
            SELECT u.phone, v.public_id, v.type, v.plate_number, v.details,
              v.insurance_doc, v.status, v.photo
             FROM vehicle v
             JOIN driver_profile dp ON v.driver_id = dp.id
             JOIN app_user u ON dp.user_id = u.id
             WHERE u.public_id = :driverId
            """)
    List<VehicleProfileView> findViewByDriverId(UUID driverId);

    @Query("""
            SELECT u.phone, v.public_id, v.type, v.plate_number, v.details,
              v.insurance_doc, v.status, v.photo
            FROM vehicle v
            JOIN driver_profile dp ON v.driver_id = dp.id
            JOIN app_user u ON dp.user_id = u.id
            """)
    List<VehicleProfileView> findAllVehicles();
}