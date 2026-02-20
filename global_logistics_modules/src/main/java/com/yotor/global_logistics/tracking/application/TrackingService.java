package com.yotor.global_logistics.tracking.application;

import com.yotor.global_logistics.assignment.api.AssignmentQueryPort;
import com.yotor.global_logistics.assignment.api.dto.AssignmentTrackingRes;
import com.yotor.global_logistics.exception.BusinessException;
import com.yotor.global_logistics.exception.ErrorCode;
import com.yotor.global_logistics.identity.domain.user.enums.UserRole;
import com.yotor.global_logistics.security.SecurityUtils;
import com.yotor.global_logistics.shipment.api.ShipmentQueryPort;
import com.yotor.global_logistics.tracking.application.dto.TrackingRecordRequest;
import com.yotor.global_logistics.tracking.application.dto.TrackingResponse;
import com.yotor.global_logistics.tracking.entity.Tracking;
import com.yotor.global_logistics.tracking.persistance.TrackingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TrackingService {

    private final TrackingRepository trackingRepository;
    private final AssignmentQueryPort assignmentQueryPort;
    private final ShipmentQueryPort shipmentQueryPort;


    @PreAuthorize("hasRole('DRIVER')")
    @Transactional
    public void recordLocation(TrackingRecordRequest req) {
        UUID driverId = SecurityUtils.currentUser().userPublicId();

        AssignmentTrackingRes assignment = assignmentQueryPort
                        .findByExternalIdForTracking(req.assignmentId());

        if (!assignment.driverId().equals(driverId)) {
            throw new BusinessException(ErrorCode.USER_NOT_ALLOWED);
        }

        if (!assignment.status().canTrack()) {
            throw new BusinessException(ErrorCode.TRACKING_NOT_ALLOWED);
        }

        // 🔎 get latest record - expensive though
        Optional<Tracking> latestOpt =
                trackingRepository.findLatest(req.assignmentId());

        if (latestOpt.isPresent()) {

            Tracking latest = latestOpt.get();

            boolean isAtTheSameLocation = isSameLocation(latest, req);

            if (isAtTheSameLocation) {
                return; // skip insert
            }
        }

        Tracking tracking =
                Tracking.create(
                        req.assignmentId(),
                        req.latitude(),
                        req.longitude(),
                        req.accuracy(),
                        req.speed(),
                        req.recordedAt()
                );

        trackingRepository.save(tracking);
    }

    private boolean isSameLocation(Tracking latest, TrackingRecordRequest req) {

        double latDiff = Math.abs(latest.getLatitude() - req.latitude());
        double lonDiff = Math.abs(latest.getLongitude() - req.longitude());

        return latDiff < 0.00001 && lonDiff < 0.00001;
    }


    @PreAuthorize("hasAnyRole('ADMIN','CONSIGNOR')")
    @Transactional(readOnly = true)
    public TrackingResponse getLatest(UUID assignmentId) {

        String role = SecurityUtils.currentUser().role();

        if(role.equals(UserRole.CONSIGNOR.name())){
            UUID shipmentId = assignmentQueryPort.getShipmentId(assignmentId);
            UUID shipmentConsignorId = shipmentQueryPort.getConsignorId(shipmentId);
            UUID currentConsignorId = SecurityUtils.currentUser().userPublicId();
            if(!shipmentConsignorId.equals(currentConsignorId)){
                throw new BusinessException(ErrorCode.USER_NOT_ALLOWED);
            }
        }

        Tracking tracking =
                trackingRepository
                        .findLatest(assignmentId)
                        .orElseThrow(() -> new BusinessException(ErrorCode.TRACKING_NOT_FOUND));

        return TrackingResponse.from(tracking);
    }

    @PreAuthorize("hasAnyRole('ADMIN','CONSIGNOR')")
    @Transactional(readOnly = true)
    public List<TrackingResponse> getRoute(UUID assignmentId) {

         String role = SecurityUtils.currentUser().role();
        if(role.equals(UserRole.CONSIGNOR.name())){
            UUID shipmentId = assignmentQueryPort.getShipmentId(assignmentId);
            UUID shipmentConsignorId = shipmentQueryPort.getConsignorId(shipmentId);
            UUID currentConsignorId = SecurityUtils.currentUser().userPublicId();
            if(!shipmentConsignorId.equals(currentConsignorId)){
                throw new BusinessException(ErrorCode.USER_NOT_ALLOWED);
            }
        }

        return trackingRepository
                .findAllByAssignment(assignmentId)
                .stream()
                .map(TrackingResponse::from)
                .toList();
    }


}
