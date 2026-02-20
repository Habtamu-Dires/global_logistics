package com.yotor.global_logistics.assignment.domain.assignment;

import com.yotor.global_logistics.assignment.domain.assignment.dto.ActorType;
import com.yotor.global_logistics.assignment.domain.assignment.dto.AssignmentStatus;
import com.yotor.global_logistics.exception.BusinessException;
import com.yotor.global_logistics.exception.ErrorCode;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static com.yotor.global_logistics.assignment.domain.assignment.dto.AssignmentStatus.*;

@Getter
@Table("shipment_assignment")
public class ShipmentAssignment {

    @Id
    private Long id;

    private UUID publicId;

    @Version
    private Long version;

    private UUID shipmentId;
    private UUID driverId;

    private AssignmentStatus status;

    private BigDecimal agreedPrice;

    private final String startLocation;
    private final Double startLatitude;
    private final Double startLongitude;

    private final UUID assignedBy;
    private final LocalDateTime assignedAt;

    private LocalDateTime loadedAt;
    private LocalDateTime startedAt;

    private LocalDateTime offloadedAt;
    private LocalDateTime grnGeneratedAt;
    private LocalDateTime consignorConfirmedAt;

    private LocalDateTime cancelledAt;
    private String cancelReason;

    @MappedCollection(idColumn = "assignment_id")
    private Set<AssignmentStatusHistory> statusHistories = new HashSet<>();

    @PersistenceCreator
    public ShipmentAssignment(
            Long id,
            Long version,
            UUID publicId,
            UUID shipmentId,
            UUID driverId,

            String status,
            BigDecimal agreedPrice,

            String startLocation,
            Double startLatitude,
            Double startLongitude,

            UUID assignedBy,
            LocalDateTime assignedAt,

            LocalDateTime loadedAt,
            LocalDateTime startedAt,

            LocalDateTime offloadedAt,
            LocalDateTime grnGeneratedAt,
            LocalDateTime consignorConfirmedAt,

            LocalDateTime cancelledAt,
            String cancelReason
    ) {
        this.id = id;
        this.version = version;
        this.publicId = publicId;
        this.shipmentId = shipmentId;
        this.driverId = driverId;

        this.status = AssignmentStatus.valueOf(status);
        this.agreedPrice = agreedPrice;

        this.startLocation = startLocation;
        this.startLatitude = startLatitude;
        this.startLongitude = startLongitude;

        this.assignedBy = assignedBy;
        this.assignedAt = assignedAt;

        this.loadedAt = loadedAt;
        this.startedAt = startedAt;

        this.offloadedAt = offloadedAt;
        this.grnGeneratedAt = grnGeneratedAt;
        this.consignorConfirmedAt = consignorConfirmedAt;

        this.cancelledAt = cancelledAt;
        this.cancelReason = cancelReason;
    }

    private ShipmentAssignment(
            UUID adminId,

            UUID shipmentId,
            UUID driverId,
            BigDecimal agreedPrice,

            String startLocation,
            Double startLatitude,
            Double startLongitude
    ) {
        this.publicId = UUID.randomUUID();
        this.shipmentId = shipmentId;
        this.driverId = driverId;
        this.agreedPrice = agreedPrice;

        this.startLocation = startLocation;
        this.startLatitude = startLatitude;
        this.startLongitude = startLongitude;

        this.status = DRIVER_ASSIGNED;
        this.assignedAt = LocalDateTime.now();
        this.assignedBy = adminId;
    }

    public static ShipmentAssignment create(
            UUID adminId,
            UUID shipmentId,
            UUID driverId,
            BigDecimal agreedPrice,
            String startLocation,
            Double startLatitude,
            Double startLongitude
    ) {
        return new ShipmentAssignment(
                adminId,
                shipmentId,
                driverId,
                agreedPrice,
                startLocation,
                startLatitude,
                startLongitude
        );
    }

    /** ---------- Domain Rule ------------ */
    public void markGdnGenerated(UUID adminId) {
        require(DRIVER_ASSIGNED, LOADING_CONFIRMED);

        changeStatus(GDN_GENERATED, adminId, ActorType.ADMIN, null);
    }

    public void confirmLoading(UUID actorId, ActorType actorType) {
        require(DRIVER_ASSIGNED,GDN_GENERATED);
        changeStatus(LOADING_CONFIRMED, actorId, actorType, null);
        this.loadedAt = LocalDateTime.now();
    }

    public void startTransport(UUID actorId, ActorType actorType) {
        handleMilestoneTransition(IN_TRANSIT, actorId, actorType);
        this.startedAt = LocalDateTime.now();
    }

    public void confirmOffloading(UUID actorId, ActorType actorType) {
        handleMilestoneTransition(OFFLOADING_CONFIRMED, actorId, actorType);
        this.offloadedAt = LocalDateTime.now();
    }

    public void markGrnGenerated(UUID actorId, ActorType actorType) {;
        handleMilestoneTransition(GRN_GENERATED, actorId, actorType);
        this.grnGeneratedAt = LocalDateTime.now();
    }

    public void confirmReceiptByConsignor(UUID consignorId) {
        handleMilestoneTransition(CONSIGNOR_CONFIRMED, consignorId, ActorType.CONSIGNOR);
        this.consignorConfirmedAt = LocalDateTime.now();
    }


    public void cancel(UUID actorId, ActorType actorType, String reason) {

        if (status.isTerminal()){
            throw new BusinessException(ErrorCode.OPERATION_NOT_ALLOWED_IN_CURRENT_STATE);
        }

        if(status.ordinal() >= IN_TRANSIT.ordinal() ){
            throw new BusinessException(ErrorCode.OPERATION_NOT_ALLOWED_IN_CURRENT_STATE);
        }

        changeStatus(CANCELLED, actorId, actorType, reason);

        this.cancelledAt = LocalDateTime.now();
        this.cancelReason = reason;
    }



    /** helper methods ------------------------------------------------ */
    private void require(AssignmentStatus... allowed) {
        if (Arrays.stream(allowed).noneMatch(s -> s == this.status)) {
            throw new BusinessException(ErrorCode.OPERATION_NOT_ALLOWED_IN_CURRENT_STATE);
        }
    }

    // handle milestone transition
    public void handleMilestoneTransition(
            AssignmentStatus targetStatus,
            UUID actorId,
            ActorType actorType
    ) {

        if (status.isTerminal()) {
            throw new BusinessException(ErrorCode.OPERATION_NOT_ALLOWED_IN_CURRENT_STATE);
        }

        AssignmentStatus requiredCurrent = MILESTONE_FLOW.get(targetStatus);

        if (requiredCurrent == null) {
            throw new IllegalArgumentException("Unsupported milestone: " + targetStatus);
        }

        // 1️⃣ First actor moves the state
        if (status == requiredCurrent) {
            changeStatus(targetStatus, actorId, actorType, null);
            return;
        }

        // 2️⃣ Second confirmation — ADMIN ONLY
        if (status == targetStatus && actorType == ActorType.ADMIN) {

            if (alreadyConfirmedBy(targetStatus)) {
                throw new BusinessException(ErrorCode.OPERATION_NOT_ALLOWED_IN_CURRENT_STATE);
            }

            if(sameActorDidTheTransition(actorId, targetStatus)){
                throw new BusinessException(ErrorCode.OPERATION_NOT_ALLOWED_IN_CURRENT_STATE);
            }

            recordConfirmation(actorId, actorType, targetStatus);
            return;
        }

        throw new BusinessException(ErrorCode.OPERATION_NOT_ALLOWED_IN_CURRENT_STATE);
    }

    ///  change assignment status
    private void changeStatus(
            AssignmentStatus newStatus,
            UUID actorId,
            ActorType actorType,
            String remark
    ) {

        AssignmentStatus previous = this.status;

        this.status = newStatus;

        AssignmentStatusHistory history =
                AssignmentStatusHistory.record(
                        previous,
                        newStatus,
                        actorId,
                        actorType,
                        remark
                );

        this.statusHistories.add(history);
    }

    private void recordConfirmation(
            UUID actorId,
            ActorType actorType,
            AssignmentStatus status
    ) {

        statusHistories.add(
                AssignmentStatusHistory.record(
                        status,
                        status,
                        actorId,
                        actorType,
                        "CONFIRMATION"
                )
        );
    }


    private boolean alreadyConfirmedBy(AssignmentStatus status) {
        return statusHistories.stream()
                .anyMatch(h ->
                        h.getToStatus() == status &&
                                h.getActorType() == ActorType.ADMIN
                );
    }

    private boolean sameActorDidTheTransition(UUID actorId, AssignmentStatus status) {
        return statusHistories.stream()
                .anyMatch(h ->
                        h.getToStatus() == status &&
                                h.getFromStatus() != status &&
                                actorId.equals(h.getChangedBy())
                );
    }


    /*** ---- admin override method */
    public void adminOverrideStatus(
            AssignmentStatus targetStatus,
            UUID adminId,
            String reason
    ) {
        if (reason == null || reason.isBlank()) {
            throw new BusinessException(ErrorCode.OPERATION_NOT_ALLOWED_IN_CURRENT_STATE);
        }

        changeStatus(
                targetStatus,
                adminId,
                ActorType.ADMIN,
                "ADMIN_OVERRIDE: " + reason
        );

    }

}

