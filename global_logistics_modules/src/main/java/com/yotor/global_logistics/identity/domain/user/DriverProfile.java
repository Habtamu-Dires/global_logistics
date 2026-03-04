package com.yotor.global_logistics.identity.domain.user;

import com.yotor.global_logistics.identity.domain.user.enums.DriverStatus;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Table("driver_profile")
public class DriverProfile {

    @Id
    private Long id;

    private Long userId;

    private String licenceNumber;
    private String licenceDocument;
    private String preferredLanes;
    private DriverStatus status;

    protected DriverProfile() {}

    public static DriverProfile create(
            Long userId,
            String licenceNumber,
            String licenceDocument,
            String preferredLanes
    ) {
        DriverProfile p = new DriverProfile();
        p.userId = userId;
        p.licenceNumber = licenceNumber;
        p.licenceDocument = licenceDocument;
        p.preferredLanes = preferredLanes;
        p.status = DriverStatus.PENDING;
        return p;
    }

    public void profileCreated(){
        this.status = DriverStatus.PROFILE_CREATED;
    }

    public void markPending(){
        this.status = DriverStatus.PENDING;
    }

    public void approve() {
        this.status = DriverStatus.APPROVED;
    }

    public void activate() {
        this.status = DriverStatus.ACTIVE;
    }

    public void assigned() {
        this.status = DriverStatus.ASSIGNED;
    }

    public void offered(){
        this.status = DriverStatus.OFFERED;
    }

    public void suspend() {
        this.status = DriverStatus.SUSPENDED;
    }

    public void reject() {
        this.status = DriverStatus.REJECTED;
    }
}
