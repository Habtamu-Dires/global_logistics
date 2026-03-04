package com.yotor.global_logistics.assignment.application.document;

import com.yotor.global_logistics.assignment.application.document.dto.CreateGdnRequest;
import com.yotor.global_logistics.assignment.application.document.dto.DocumentType;
import com.yotor.global_logistics.assignment.domain.assignment.ShipmentAssignment;
import com.yotor.global_logistics.assignment.domain.document.Gdn;
import com.yotor.global_logistics.assignment.domain.document.dto.AssignmentSnapshot;
import com.yotor.global_logistics.assignment.persistence.AssignmentRepository;
import com.yotor.global_logistics.assignment.persistence.GdnRepository;
import com.yotor.global_logistics.identity.port.IdentityQueryPort;
import com.yotor.global_logistics.identity.application.identity.dto.UserProfile;
import com.yotor.global_logistics.identity.application.vehicle.dto.VehicleSummery;
import com.yotor.global_logistics.security.SecurityUtils;
import com.yotor.global_logistics.shipment.port.ShipmentQueryPort;
import com.yotor.global_logistics.shipment.application.shipment.dto.ShipmentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GdnService {

    private final DocumentNumberGenerator numberGenerator;
    private final AssignmentRepository assignmentRepository;
    private final GdnRepository gdnRepository;
    private final ShipmentQueryPort shipmentQueryPort;
    private final IdentityQueryPort identityQueryPort;

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public UUID generate(UUID assignmentPublicId, CreateGdnRequest req) {
        UUID adminId = SecurityUtils.currentUser().userPublicId();

        ShipmentAssignment assignment =
                assignmentRepository.findByPublicId(assignmentPublicId)
                        .orElseThrow();

        String docNumber = numberGenerator.next(DocumentType.GDN);

        AssignmentSnapshot snapshot = snapshotBuilder(assignment);

        Gdn gdn = Gdn.generate(
                docNumber,
                assignment.getId(),
                assignment.getShipmentId(),
                adminId,
                snapshot,
                req
        );

        gdnRepository.save(gdn);

        assignment.markGdnGenerated(adminId);

        assignmentRepository.save(assignment);

        return gdn.getPublicId();
    }

    private AssignmentSnapshot snapshotBuilder(ShipmentAssignment assignment){
        ShipmentResponse shipment = shipmentQueryPort.getShipmentDetails(assignment.getShipmentId());
        UserProfile consignor = identityQueryPort.getUserSummary(shipment.consignorId());
        UserProfile driver = identityQueryPort.getUserSummary(assignment.getDriverId());
        VehicleSummery vehicleSummery = identityQueryPort.getVehicleSummery(driver.publicId());

        return AssignmentSnapshot.builder()
                .consignorName(consignor.firstName() + " " + consignor.lastName())
                .consignorPhone(consignor.phone())
                .driverName(driver.firstName() + " " + driver.lastName())
                .driverPhone(driver.phone())
                .vehicleType(vehicleSummery.type())
                .vehiclePlateNo(vehicleSummery.plateNumber())
                .goodsType(shipment.goodType())
                .loadingLocation(shipment.loadingLocation())
                .loadingDate(assignment.getLoadedAt())
                .offloadingLocation(shipment.offloadingLocation())
                .build();
    }

}

