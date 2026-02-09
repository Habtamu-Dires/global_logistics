package com.yotor.global_logestics.shipment.application;

import com.yotor.global_logestics.exception.BusinessException;
import com.yotor.global_logestics.exception.ErrorCode;
import com.yotor.global_logestics.identity.api.IdentityQueryService;
import com.yotor.global_logestics.security.SecurityUtils;
import com.yotor.global_logestics.shipment.application.dto.*;
import com.yotor.global_logestics.shipment.domain.Shipment;
import com.yotor.global_logestics.shipment.persistence.ShipmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ShipmentService {

    private final ShipmentRepository shipmentRepo;
    private final IdentityQueryService identityQueryService;

    @PreAuthorize("hasRole('CONSIGNOR')")
    public UUID createShipment(CreateShipmentRequest request) {
        UUID consignorId = SecurityUtils.currentUser().userExternalId();

        boolean userApproved = identityQueryService.isUserApproved(consignorId);
        if(!userApproved){ throw new BusinessException(ErrorCode.CONSIGNOR_NOT_APPROVED);}

        Shipment shipment = Shipment.create(
                consignorId,
                request.price(),
                request.goodType(),
                request.weight(),
                request.volume(),
                request.loadingLocation(),
                request.offloadingLocation(),
                request.requiredVehicleType(),
                request.requiredVehicleNumber(),
                request.loadingDate(),
                request.deliveryDate(),
                request.details()
        );

        shipmentRepo.save(shipment);

        return shipment.getExternalId();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void adminRequestsChange(AdminRequestChange req) {
        UUID adminId = SecurityUtils.currentUser().userExternalId();
        Shipment shipment = shipmentRepo
                .findByExternalId(req.shipmentId())
                .orElseThrow(() -> new BusinessException(ErrorCode.SHIPMENT_NOT_FOUND));

        shipment.adminRequestsChange(
                adminId,
                req.priceAmount(),
                req.requiredVehicleNumber(),
                req.requiredVehicleType(),
                req.loadingDate(),
                req.deliveryDate(),
                req.reason()
        );

        shipmentRepo.save(shipment);
    }

    @PreAuthorize("hasRole('CONSIGNOR')")
    public void consignorCounter(ConsignorCounterReq req){
        UUID consignorId = SecurityUtils.currentUser().userExternalId();
        Shipment shipment = shipmentRepo
                .findByExternalIdAndConsignorId(req.shipmentId(),consignorId)
                .orElseThrow(()-> new BusinessException(ErrorCode.SHIPMENT_NOT_FOUND));

        shipment.consignorCounters(
                req.counterPrice(),
                req.requiredVehicleType(),
                req.requiredVehicleNumber(),
                req.loadingDate(),
                req.deliveryDate(),
                req.reason()
        );

        shipmentRepo.save(shipment);

    }

    @PreAuthorize("hasRole('CONSIGNOR')")
    public void consignorAccepts(ConsignorRequest req){
        UUID consignorId = SecurityUtils.currentUser().userExternalId();
        Shipment shipment = shipmentRepo
                .findByExternalIdAndConsignorId(req.shipmentId(),consignorId)
                .orElseThrow(()-> new BusinessException(ErrorCode.SHIPMENT_NOT_FOUND));

        shipment.consignorAccepts(req.reason());

        shipmentRepo.save(shipment);
    }

    @PreAuthorize("hasRole('CONSIGNOR')")
    public void consignorRejects(ConsignorRequest req){
        UUID consignorId = SecurityUtils.currentUser().userExternalId();
        Shipment shipment = shipmentRepo
                .findByExternalIdAndConsignorId(req.shipmentId(),consignorId)
                .orElseThrow(()-> new BusinessException(ErrorCode.SHIPMENT_NOT_FOUND));

        shipment.consignorRejectsOffer(req.reason());

        shipmentRepo.save(shipment);
    }

    @PreAuthorize("hasRole('CONSIGNOR')")
    public void consignorCancel(ConsignorRequest req){
        UUID consignorId = SecurityUtils.currentUser().userExternalId();
        Shipment shipment = shipmentRepo
                .findByExternalIdAndConsignorId(req.shipmentId(),consignorId)
                .orElseThrow(()-> new BusinessException(ErrorCode.SHIPMENT_NOT_FOUND));

        shipment.cancelByConsignor(req.reason());

        shipmentRepo.save(shipment);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void adminApprove(AdminRequest req){
        UUID adminId = SecurityUtils.currentUser().userExternalId();
        Shipment shipment = shipmentRepo
                .findByExternalId(req.shipmentId())
                .orElseThrow(() -> new BusinessException(ErrorCode.SHIPMENT_NOT_FOUND));

        shipment.approve(adminId);

        shipmentRepo.save(shipment);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void adminRejects(AdminRequest req){

        UUID adminId = SecurityUtils.currentUser().userExternalId();
        Shipment shipment = shipmentRepo
                .findByExternalId(req.shipmentId())
                .orElseThrow(() -> new BusinessException(ErrorCode.SHIPMENT_NOT_FOUND));

        shipment.adminRejectsOffer(adminId, req.reason());

        shipmentRepo.save(shipment);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void adminCancel(AdminRequest req){
        UUID adminId = SecurityUtils.currentUser().userExternalId();
        Shipment shipment = shipmentRepo
                .findByExternalId(req.shipmentId())
                .orElseThrow(() -> new BusinessException(ErrorCode.SHIPMENT_NOT_FOUND));

        shipment.cancelByAdmin(adminId, req.reason());

        shipmentRepo.save(shipment);
    }

    /**--- admin get requests --- */
    @PreAuthorize("hasRole('CONSIGNOR')")
    public List<ShipmentResponse> getConsignorShipments(){
        UUID consignorId = SecurityUtils.currentUser().userExternalId();
        return shipmentRepo.findByConsignorId(consignorId)
                .stream().map(ShipmentResponse::from)
                .toList();
    }

    /** consignor get requests  */
    @PreAuthorize("hasRole('ADMIN')")
    public List<ShipmentResponse> getAllShipments(){
        return shipmentRepo.findAllShipments()
                .stream().map(ShipmentResponse::from)
                .toList();
    }
}

