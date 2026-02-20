package com.yotor.global_logistics.driver_negotiation.application;

import com.yotor.global_logistics.driver_negotiation.application.dto.*;
import com.yotor.global_logistics.driver_negotiation.domain.DriverNegotiation;
import com.yotor.global_logistics.driver_negotiation.domain.dto.NegotiationStatus;
import com.yotor.global_logistics.driver_negotiation.persistence.DriverNegotiationRepository;
import com.yotor.global_logistics.exception.BusinessException;
import com.yotor.global_logistics.exception.ErrorCode;
import com.yotor.global_logistics.identity.api.IdentityQueryService;
import com.yotor.global_logistics.security.SecurityUtils;
import com.yotor.global_logistics.shipment.api.ShipmentQueryPort;
import com.yotor.global_logistics.shipment.application.dto.ShipmentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DriverNegotiationService {

    private final DriverNegotiationRepository driverNegotiationRepo;
    private final ShipmentQueryPort shipmentQueryPort;
    private final IdentityQueryService identityQueryService;

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void adminSendInitialDriverOffers(AdminInitialDriverOfferRequest req){

        if(!shipmentQueryPort.isShipmentOpenToDriverAssignment(req.shipmentId())){
            throw new BusinessException(ErrorCode.SHIPMENT_NOT_READY_FOR_DRIVER_ASSIGNMENT);
        }

        for (UUID driverId : req.driverIds()) {
            if (!identityQueryService.isDriverApproved(driverId)) {
                throw new BusinessException(ErrorCode.DRIVER_NOT_APPROVED);
            }

            if(driverNegotiationRepo.existsByShipmentIdAndDriverId(req.shipmentId(), driverId)){
                throw new BusinessException(ErrorCode.DRIVER_ALREADY_ASSIGNED);
            }

            DriverNegotiation driverNegotiation = DriverNegotiation
                    .adminSendInitialOffer(
                            req.shipmentId(),
                            req.offeredPrice(),
                            driverId,
                            req.remark()
                    );

            driverNegotiationRepo.save(driverNegotiation);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void adminCounter(AdminCounterRequest req){
        UUID adminId = SecurityUtils.currentUser().userPublicId();

        if(!shipmentQueryPort.isShipmentOpenToDriverAssignment(req.shipmentId())){
            throw new BusinessException(ErrorCode.OPERATION_NOT_ALLOWED_IN_CURRENT_STATE);
        }

        for (UUID driverId : req.driverIds()) {
            if (!identityQueryService.isDriverApproved(driverId)) {
                throw new BusinessException(ErrorCode.DRIVER_NOT_VERIFIED);
            }

            DriverNegotiation negotiation = driverNegotiationRepo
                    .findByShipmentIdAndDriverId(req.shipmentId(),driverId)
                    .orElseThrow(()-> new BusinessException(ErrorCode.OFFER_NOT_FOUND));

            negotiation.adminCounter(req.offeredPrice(),adminId,req.remark());

            driverNegotiationRepo.save(negotiation);
        }
    }

    @PreAuthorize("hasRole('DRIVER')")
    public void driverCounter(DriverCounterRequest req){
        UUID driverId = SecurityUtils.currentUser().userPublicId();
        UUID negotiationId = req.negotiationId();
        DriverNegotiation negotiation = driverNegotiationRepo
                .findByNegotiationId(negotiationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.OFFER_NOT_FOUND));

        negotiation.driverCounter(req.counterPrice(),driverId,req.reason());
        driverNegotiationRepo.save(negotiation);
    }

    @PreAuthorize("hasRole('DRIVER')")
    public void driverAcceptOffer(DriverAcceptRequest req){
        DriverNegotiation negotiation = driverNegotiationRepo.findByNegotiationId(req.negotiationId())
                . orElseThrow(() -> new BusinessException(ErrorCode.OFFER_NOT_FOUND));

        negotiation.driverAccept(req.lat(),req.lon(),req.locationText());
        driverNegotiationRepo.save(negotiation);
    }

    @PreAuthorize("hasRole('DRIVER')")
    public void driverRejectOffer(RejectOrCancelRequest req){
        DriverNegotiation negotiation = driverNegotiationRepo.findByNegotiationId(req.negotiationId())
                .orElseThrow(() -> new BusinessException(ErrorCode.OFFER_NOT_FOUND));

        negotiation.driverReject(req.reason());
        driverNegotiationRepo.save(negotiation);
    }

    @PreAuthorize("hasRole('DRIVER')")
    public void driverCancel(RejectOrCancelRequest req){
        DriverNegotiation negotiation = driverNegotiationRepo.findByNegotiationId(req.negotiationId())
                .orElseThrow(() -> new BusinessException(ErrorCode.OFFER_NOT_FOUND));

        negotiation.driverCancel(req.reason());
        driverNegotiationRepo.save(negotiation);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void adminCancel(RejectOrCancelRequest req){
        UUID negotiationId = req.negotiationId();
        DriverNegotiation negotiation = driverNegotiationRepo.findByNegotiationId(negotiationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.OFFER_NOT_FOUND));

        negotiation.adminCancel(req.reason());
    }

    /** --- get requests -- */
    @PreAuthorize("hasRole('DRIVER')")
    public List<DriverOfferView> getActiveDriverOffers(){
        UUID driverId = SecurityUtils.currentUser().userPublicId();
        List<NegotiationStatus> activeStatus = NegotiationStatus.getActiveStatuses();

        List<DriverNegotiation> activeOffers = driverNegotiationRepo
                .findOffersByDriverIdAndStatus(driverId, activeStatus);

        List<DriverOfferView> offerViews = new ArrayList<>();

        for (DriverNegotiation offer : activeOffers) {
            ShipmentResponse shipmentResponse = shipmentQueryPort
                    .getShipmentDetails(offer.getShipmentId());

            offerViews.add(DriverOfferView.from(shipmentResponse,offer));
        }

        return offerViews;
    }

    // admins view for driver negotiations
    @PreAuthorize("hasRole('ADMIN')")
    public List<DriverNegotiationResponse> getDriverNegotiations(UUID shipmentId){
        return driverNegotiationRepo.findByShipmentId(shipmentId)
                .stream()
                .map(DriverNegotiationResponse::from)
                .toList();
    }

}
