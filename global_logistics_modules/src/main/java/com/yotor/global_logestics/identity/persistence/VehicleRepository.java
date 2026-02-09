package com.yotor.global_logestics.identity.persistence;

import com.yotor.global_logestics.identity.domain.vehicle.Vehicle;
import com.yotor.global_logestics.identity.vehicle.dto.VehicleProfileView;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VehicleRepository
        extends CrudRepository<Vehicle, Long> {

    Optional<Vehicle> findByExternalId(UUID externalId);

    Iterable<Vehicle> findByDriverId(Long driverId);

    @Query("""
            SELECT u.phone, v.external_id, v.type, v.plate_number, v.details,
              v.insurance_doc, v.status, v.photo
             FROM vehicle v
             JOIN driver_profile dp ON v.driver_id = dp.id
             JOIN app_user u ON dp.user_id = u.id
             WHERE u.external_id = :driverId
            """)
    List<VehicleProfileView> findViewByDriverId(UUID driverId);

    @Query("""
            SELECT u.phone, v.external_id, v.type, v.plate_number, v.details,
              v.insurance_doc, v.status, v.photo
            FROM vehicle v
            JOIN driver_profile dp ON v.driver_id = dp.id
            JOIN app_user u ON dp.user_id = u.id
            """)
    List<VehicleProfileView> findAllVehicles();
}