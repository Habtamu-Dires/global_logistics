package com.yotor.global_logistics.identity.domain.vehicle;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;


@Table("vehicle")
public class Vehicle {

    @Id
    private Long id;

    private final UUID publicId;

    private final Long driverId;

    private final String plateNumber;
    private final String type;
    private final String insuranceDoc;
    private VehicleStatus status;
    private final String details;
    private final String photo;

    private final LocalDateTime createdAt;


    @PersistenceCreator
    public Vehicle(
            Long id,
            UUID publicId,
            Long driverId,
            String plateNumber,
            String type,
            String status,
            String insuranceDoc,
            String photo,
            String details,
            LocalDateTime createdAt
    ) {
        this.id = id;
        this.publicId = publicId;
        this.driverId = driverId;
        this.plateNumber = plateNumber;
        this.type = type;
        this.status = VehicleStatus.valueOf(status);
        this.photo = photo;
        this.insuranceDoc = insuranceDoc;
        this.details = details;
        this.createdAt = createdAt;
    }

    public Vehicle(
            Long driverId,
            String plateNumber,
            String type,
            String insuranceDoc,
            String details,
            String photo
    ){
        this.publicId = UUID.randomUUID();
        this.driverId = driverId;
        this.plateNumber = plateNumber;
        this.type = type;
        this.insuranceDoc = insuranceDoc;
        this.details = details;
        this.photo = photo;
        this.status = VehicleStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    /* ---- Domain Rule -------------*/
    public static Vehicle create(
            Long driverId,
            String plateNumber,
            String type,
            String insuranceDoc,
            String details,
            String photo
    ){
        return new Vehicle(
                driverId,
                plateNumber,
                type,
                insuranceDoc,
                details,
                photo
        );
    }

    public void approve(){
        this.status = VehicleStatus.APPROVED;
    }

    public void reject(){
        this.status = VehicleStatus.REJECTED;
    }

    public void suspend(){
        this.status = VehicleStatus.INACTIVE;
    }

    public boolean isApproved() {
        return this.status == VehicleStatus.APPROVED;
    }
}
