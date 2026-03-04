package com.yotor.global_logistics.shipment.application.shipment;

import com.yotor.global_logistics.common.PageResponse;
import com.yotor.global_logistics.exception.BusinessException;
import com.yotor.global_logistics.exception.ErrorCode;
import com.yotor.global_logistics.identity.port.IdentityQueryPort;
import com.yotor.global_logistics.security.SecurityUtils;
import com.yotor.global_logistics.shipment.application.shipment.dto.*;
import com.yotor.global_logistics.shipment.application.shipment.enums.ShipmentStage;
import com.yotor.global_logistics.shipment.domain.Shipment;
import com.yotor.global_logistics.shipment.domain.enums.ShipmentStatus;
import com.yotor.global_logistics.shipment.persistence.ShipmentRepository;
import com.yotor.global_logistics.shipment_finance.api.ShipmentFinancePort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static com.yotor.global_logistics.shipment.domain.enums.ShipmentStatus.*;

@Service
@RequiredArgsConstructor
public class ShipmentService {

    private final ShipmentRepository shipmentRepo;
    private final IdentityQueryPort identityQueryPort;
    private final ShipmentFinancePort shipmentFinancePort;

    @PreAuthorize("hasRole('CONSIGNOR')")
    public UUID createShipment(CreateShipmentRequest req) {
        UUID consignorId = SecurityUtils.currentUser().userPublicId();

        boolean consignorApproved = identityQueryPort.isConsignorApproved(consignorId);
        if(!consignorApproved){ throw new BusinessException(ErrorCode.CONSIGNOR_NOT_APPROVED);}

        Shipment shipment = Shipment.create(
                consignorId,
                req.price(),
                req.priceType(),
                req.goodType(),
                req.quantity(),
                req.weight(),
                req.volume(),
                req.loadingLocation(),
                req.offloadingLocation(),
                req.route(),
                req.requiredVehicleType(),
                req.requiredVehicleNumber(),
                req.loadingDate(),
                req.deliveryDate(),
                req.details()
        );

        shipmentRepo.save(shipment);

        return shipment.getPublicId();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void adminRequestsChange(AdminRequestChange req) {
        UUID adminId = SecurityUtils.currentUser().userPublicId();
        Shipment shipment = shipmentRepo
                .findByPublicId(req.shipmentId())
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
        UUID consignorId = SecurityUtils.currentUser().userPublicId();
        Shipment shipment = shipmentRepo
                .findByPublicIdAndConsignorId(req.shipmentId(),consignorId)
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
        UUID consignorId = SecurityUtils.currentUser().userPublicId();
        Shipment shipment = shipmentRepo
                .findByPublicIdAndConsignorId(req.shipmentId(),consignorId)
                .orElseThrow(()-> new BusinessException(ErrorCode.SHIPMENT_NOT_FOUND));

        shipment.consignorAccepts(req.reason());

        shipmentRepo.save(shipment);
    }

    @PreAuthorize("hasRole('CONSIGNOR')")
    public void consignorRejects(ConsignorRequest req){
        UUID consignorId = SecurityUtils.currentUser().userPublicId();
        Shipment shipment = shipmentRepo
                .findByPublicIdAndConsignorId(req.shipmentId(),consignorId)
                .orElseThrow(()-> new BusinessException(ErrorCode.SHIPMENT_NOT_FOUND));

        shipment.consignorRejectsOffer(req.reason());

        shipmentRepo.save(shipment);
    }

    @PreAuthorize("hasRole('CONSIGNOR')")
    public void consignorCancel(ConsignorRequest req){
        UUID consignorId = SecurityUtils.currentUser().userPublicId();
        Shipment shipment = shipmentRepo
                .findByPublicIdAndConsignorId(req.shipmentId(),consignorId)
                .orElseThrow(()-> new BusinessException(ErrorCode.SHIPMENT_NOT_FOUND));

        shipment.cancelByConsignor(req.reason());

        shipmentRepo.save(shipment);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void adminApprove(AdminRequest req){
        UUID adminId = SecurityUtils.currentUser().userPublicId();
        Shipment shipment = shipmentRepo
                .findByPublicId(req.shipmentId())
                .orElseThrow(() -> new BusinessException(ErrorCode.SHIPMENT_NOT_FOUND));

        shipment.approve(adminId);
        shipmentFinancePort.createFinanceForShipment(
                shipment.getPublicId(),
                shipment.getPriceAmount()
        );

        shipmentRepo.save(shipment);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void adminRejects(AdminRequest req){

        UUID adminId = SecurityUtils.currentUser().userPublicId();
        Shipment shipment = shipmentRepo
                .findByPublicId(req.shipmentId())
                .orElseThrow(() -> new BusinessException(ErrorCode.SHIPMENT_NOT_FOUND));

        shipment.adminRejectsOffer(adminId, req.reason());

        shipmentRepo.save(shipment);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void adminCancel(AdminRequest req){
        UUID adminId = SecurityUtils.currentUser().userPublicId();
        Shipment shipment = shipmentRepo
                .findByPublicId(req.shipmentId())
                .orElseThrow(() -> new BusinessException(ErrorCode.SHIPMENT_NOT_FOUND));

        shipment.cancelByAdmin(adminId, req.reason());

        shipmentRepo.save(shipment);
    }

    /**--- admin get requests --- */
    @PreAuthorize("hasRole('CONSIGNOR')")
    public List<ShipmentResponse> getConsignorShipments(){
        UUID consignorId = SecurityUtils.currentUser().userPublicId();
        return shipmentRepo.findByConsignorId(consignorId)
                .stream().map(ShipmentResponse::from)
                .toList();
    }


    @PreAuthorize("hasRole('ADMIN')")
    public PageResponse<ShipmentSummary> getPageOfShipmentsByStage(ShipmentStage stage, int page, int size){
        Pageable pageable = PageRequest.of(page,size);

        List<ShipmentStatus> statuses = stage.statuses();

       var res = shipmentRepo.findPageOfShipmentsByStatus(
                statuses, pageable.getPageSize(),pageable.getOffset());


       long total = shipmentRepo.countShipmentByStatus(statuses);

       return PageResponse.toPage(res,total,pageable);
    }


}

