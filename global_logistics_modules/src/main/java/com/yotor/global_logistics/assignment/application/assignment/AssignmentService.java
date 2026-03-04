package com.yotor.global_logistics.assignment.application.assignment;

import com.yotor.global_logistics.assignment.application.assignment.dto.*;
import com.yotor.global_logistics.assignment.domain.assignment.dto.ActorType;
import com.yotor.global_logistics.assignment.event.AssignmentCreatedEvent;
import com.yotor.global_logistics.driver_negotiation.api.DriverNegotiationQueryService;
import com.yotor.global_logistics.driver_negotiation.application.dto.DriverNegotiationResponse;
import com.yotor.global_logistics.assignment.domain.assignment.ShipmentAssignment;
import com.yotor.global_logistics.driver_negotiation.domain.dto.NegotiationStatus;
import com.yotor.global_logistics.assignment.persistence.AssignmentRepository;
import com.yotor.global_logistics.exception.BusinessException;
import com.yotor.global_logistics.exception.ErrorCode;
import com.yotor.global_logistics.security.SecurityUtils;
import com.yotor.global_logistics.shipment.port.ShipmentQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static com.yotor.global_logistics.assignment.domain.assignment.dto.AssignmentStatus.IN_TRANSIT;

@Service
@RequiredArgsConstructor
public class AssignmentService {

    private final ShipmentQueryPort shipmentQueryPort;
    private final DriverNegotiationQueryService driverNegotiationQueryService;
    private final AssignmentRepository assignmentRepo;
    private final ApplicationEventPublisher publisher;

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public IdResponse selectDriver(AdminDriverSelectRequest req) {
        UUID adminId = SecurityUtils.currentUser().userPublicId();

        DriverNegotiationResponse negotiation = driverNegotiationQueryService.markSelected(req.negotiationId());

        ShipmentAssignment assignment =
                ShipmentAssignment.create(
                        adminId,
                        negotiation.shipmentId(),
                        negotiation.driverId(),
                        negotiation.finalAgreedPrice(),
                        negotiation.startLocation(),
                        negotiation.startLatitude(),
                        negotiation.startLongitude()
                );

        assignmentRepo.save(assignment);

        enforceRequiredVehicleCount(negotiation.shipmentId());

        // sned notification
        publisher.publishEvent(new AssignmentCreatedEvent(
                assignment.getPublicId(),
                assignment.getDriverId(),
                assignment.getShipmentId()
        ));


        return  new IdResponse(assignment.getPublicId());
    }

    private void enforceRequiredVehicleCount(UUID shipmentId) {

        int required =
                shipmentQueryPort.getRequiredVehicleNumber(shipmentId);

        int selectedCount =
                driverNegotiationQueryService.countByShipmentAndStatus(
                        shipmentId,
                        NegotiationStatus.SELECTED
                );

        if (selectedCount == required) {
            // mark shipment status as assigned driver
            UUID adminId = SecurityUtils.currentUser().userPublicId();
            shipmentQueryPort.markDriverAssigned(shipmentId, adminId);

            // notify drivers that accept the offer not selected
            driverNegotiationQueryService.markDriversNotSelected(shipmentId);

            // mark negotiations as expired for others offer_send, admin_countered, driver_countered
            driverNegotiationQueryService.markOthersExpired(shipmentId);
        } else if(selectedCount > required){
            throw new BusinessException(ErrorCode.REQUIRED_VEHICLE_REACHED);
        }
    }

    //  domain methods
    @PreAuthorize("hasAnyRole('ADMIN','DRIVER'')")
    @Transactional
    public void confirmLoading(AssignmentRequest req){
        UUID actorId = SecurityUtils.currentUser().userPublicId();
        String role = SecurityUtils.currentUser().roles().stream().findFirst().orElse("");
        ShipmentAssignment assignment = assignmentRepo.findByPublicId(req.assignmentId())
                .orElseThrow(() -> new BusinessException(ErrorCode.ASSIGNMENT_NOT_FOUND));

        assignment.confirmLoading(actorId, ActorType.valueOf(role));
        assignmentRepo.save(assignment);
    }


    @PreAuthorize("hasAnyRole('ADMIN,DRIVER')")
    @Transactional
    public void startTransport(AssignmentRequest req){
        UUID actorId = SecurityUtils.currentUser().userPublicId();
        String role = SecurityUtils.currentUser().roles().stream().findFirst().orElse("");
        ShipmentAssignment assignment = assignmentRepo.findByPublicId(req.assignmentId())
                .orElseThrow(() -> new BusinessException(ErrorCode.ASSIGNMENT_NOT_FOUND));

        assignment.startTransport(actorId,ActorType.valueOf(role));

        UUID shipmentId = assignment.getShipmentId();

        boolean anyInTransit =
                assignmentRepo.existsByShipmentIdAndStatus(
                        shipmentId,
                        IN_TRANSIT
                );

        if (!anyInTransit && role.equals("ADMIN")) {
            shipmentQueryPort.markInTransit(shipmentId, actorId);
        }

        assignmentRepo.save(assignment);
    }

    @PreAuthorize("hasRole('DRIVER')")
    public void confirmOffloading(AssignmentRequest req){
        UUID actorId = SecurityUtils.currentUser().userPublicId();
        String role = SecurityUtils.currentUser().roles().stream().findFirst().orElse("");
        ShipmentAssignment assignment = assignmentRepo.findByPublicId(req.assignmentId())
                .orElseThrow(() -> new BusinessException(ErrorCode.ASSIGNMENT_NOT_FOUND));

        assignment.confirmOffloading(actorId,ActorType.valueOf(role));
        assignmentRepo.save(assignment);
    }


    @PreAuthorize("hasRole('CONSIGNOR')")
    public void confirmReceiptByConsignor(AssignmentRequest req){
        UUID consignorId = SecurityUtils.currentUser().userPublicId();
        ShipmentAssignment assignment = assignmentRepo.findByPublicId(req.assignmentId())
                .orElseThrow(() -> new BusinessException(ErrorCode.ASSIGNMENT_NOT_FOUND));

        UUID shipmentConsignorId = shipmentQueryPort
                .getConsignorId(assignment.getShipmentId());

        if(!shipmentConsignorId.equals(consignorId)){
            throw new BusinessException(ErrorCode.USER_NOT_ALLOWED);
        }

        assignment.confirmReceiptByConsignor(consignorId);
        assignmentRepo.save(assignment);
    }

    @PreAuthorize("hasAnyRole('ADMIN',DRIVER')")
    public void cancelAssignment(AssignmentRequest req){
        ShipmentAssignment assignment = assignmentRepo.findByPublicId(req.assignmentId())
                .orElseThrow(() -> new BusinessException(ErrorCode.ASSIGNMENT_NOT_FOUND));

        UUID actorId = SecurityUtils.currentUser().userPublicId();
        String role = SecurityUtils.currentUser().roles().stream().findFirst().orElse("");

        assignment.cancel(actorId,ActorType.valueOf(role),req.remark());
        assignmentRepo.save(assignment);
    }

    // admin override
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Transactional
    public void adminCorrectAssignmentStatus(AdminOverrideRequest req) {

        ShipmentAssignment assignment =
                assignmentRepo.findByPublicId(req.assignmentId())
                        .orElseThrow(() -> new BusinessException(ErrorCode.ASSIGNMENT_NOT_FOUND));

        UUID adminId = SecurityUtils.currentUser().userPublicId();

        assignment.adminOverrideStatus(
                req.targetStatus(),
                adminId,
                req.reason()
        );
        assignmentRepo.save(assignment);
    }





    /** --- get requests ----------------------------------------------------------------- */
    @PreAuthorize("hasRole('ADMIN')")
    public List<AssignmentAdminView> getAssignmentByShipmentId(UUID shipmentId) {
         return assignmentRepo.findByShipmentId(shipmentId)
                 .stream()
                 .map(AssignmentAdminView::from)
                 .toList();
    }

    @PreAuthorize("hasRole('DRIVER')")
    public List<AssignmentDriverView> getAssignmentsOfDriver(){
        UUID driverId = SecurityUtils.currentUser().userPublicId();
        return assignmentRepo.findByDriverId(driverId)
                .stream()
                .map(AssignmentDriverView::from)
                .toList();
    }

}
