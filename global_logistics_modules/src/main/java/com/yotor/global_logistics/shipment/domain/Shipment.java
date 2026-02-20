package com.yotor.global_logistics.shipment.domain;

import com.yotor.global_logistics.exception.BusinessException;
import com.yotor.global_logistics.exception.ErrorCode;
import com.yotor.global_logistics.shipment.domain.dto.ShipmentStatus;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Getter
@Table("shipment")
public class Shipment {

    @Id
    private Long id;

    private final UUID publicId;
    private final UUID consignorId;

    private ShipmentStatus currentStatus;

    private BigDecimal priceAmount;
    private String priceType;
    private String priceCurrency;

    private String goodType;
    private Integer quantity;
    private String weight;
    private String volume;

    private String loadingLocation;
    private String offloadingLocation;
    private String route;

    private String requiredVehicleType;
    private int requiredVehicleNumber;

    private LocalDateTime loadingDate;
    private LocalDateTime deliveryDate;

    private String details;

    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @MappedCollection(idColumn = "shipment_id")
    private Set<ShipmentOffer> offers = new HashSet<>();

    @MappedCollection(idColumn = "shipment_id")
    private Set<ShipmentStatusHistory> statusHistory = new HashSet<>();

    private Shipment(
            UUID publicId,
            UUID consignorId,
            BigDecimal priceAmount,
            String priceType,
            String goodType,
            Integer quantity,
            String weight,
            String volume,
            String loadingLocation,
            String offloadingLocation,
            String route,
            String requiredVehicleType,
            int requiredVehicleNumber,
            LocalDateTime loadingDate,
            LocalDateTime deliveryDate,
            String details,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        if (!loadingDate.isBefore(deliveryDate)) {
            throw new BusinessException(ErrorCode.INVALID_SHIPMENT_DATES);
        }

        this.publicId = publicId;
        this.consignorId = consignorId;
        this.priceAmount = priceAmount;
        this.priceType = priceType;
        this.priceCurrency = "ETB";
        this.goodType = goodType;
        this.quantity = quantity;
        this.weight = weight;
        this.volume = volume;
        this.loadingLocation = loadingLocation;
        this.offloadingLocation = offloadingLocation;
        this.route = route;
        this.requiredVehicleType = requiredVehicleType;
        this.requiredVehicleNumber = requiredVehicleNumber;
        this.loadingDate = loadingDate;
        this.deliveryDate = deliveryDate;
        this.details = details;

        this.createdAt = createdAt;
        this.updatedAt = updatedAt;

        recordStatus(ShipmentStatus.CREATED, consignorId, "Shipment created");
    }
    /** -- factory method ---- */
    public static Shipment create(
            UUID consignorId,
            BigDecimal priceAmount,
            String priceType,
            String goodType,
            Integer quantity,
            String weight,
            String volume,
            String loadingLocation,
            String offloadingLocation,
            String route,
            String requiredVehicleType,
            int requiredVehicleNumber,
            LocalDateTime loadingDate,
            LocalDateTime deliveryDate,
            String details
    ) {
        return new Shipment(
                UUID.randomUUID(),
                consignorId,
                priceAmount,
                priceType,
                goodType,
                quantity,
                weight,
                volume,
                loadingLocation,
                offloadingLocation,
                route,
                requiredVehicleType,
                requiredVehicleNumber,
                loadingDate,
                deliveryDate,
                details,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    /** Admin & Consignor actions */
    public void adminRequestsChange(
            UUID adminId,
            BigDecimal newPrice,
            int newVehicleNumber,
            String newVehicleType,
            LocalDateTime loadingDate,
            LocalDateTime deliveryDate,
            String reason
    ) {
        assertNegotiable();

        if(deliveryDate.isBefore(loadingDate)){
            throw new BusinessException(ErrorCode.INVALID_SHIPMENT_DATES);
        }

        offers.add(
                createOffer(
                        newPrice,
                        newVehicleType,
                        newVehicleNumber,
                        loadingDate,
                        deliveryDate,
                        reason,
                        adminId
                )
        );

        recordStatus(ShipmentStatus.ADMIN_REQUESTED_CHANGE, adminId, reason);
    }

    // admin reject offer
    public void adminRejectsOffer(UUID adminId,String reason) {
        assertNegotiable();
        assertLatestOfferExists();

        if(currentStatus == ShipmentStatus.CONSIGNOR_REJECTED_OFFER
                || currentStatus == ShipmentStatus.ADMIN_REJECTED_OFFER
        ){
            throw new BusinessException(ErrorCode.SHIPMENT_OFFER_ALREADY_REJECTED);
        }

        ShipmentOffer latestOffer = latestOffer();

        if(latestOffer.getOfferedBy().equals(adminId)){
            throw new BusinessException(ErrorCode.OPERATION_NOT_ALLOWED_IN_CURRENT_STATE);
        }

        recordStatus(
                ShipmentStatus.ADMIN_REJECTED_OFFER,
                adminId,
                reason
        );
    }

    public void consignorCounters(
            BigDecimal counterPrice,
            String newVehicleType,
            int counterVehicleNumber,
            LocalDateTime loadingDate,
            LocalDateTime deliveryDate,
            String reason
    ) {
        assertNegotiable();

        ShipmentOffer latestOffer = latestOffer();
        if(latestOffer.getOfferedBy().equals(consignorId)){
            throw new BusinessException(ErrorCode.OPERATION_NOT_ALLOWED_IN_CURRENT_STATE);
        }

        if(deliveryDate.isBefore(loadingDate)){
            throw new BusinessException(ErrorCode.INVALID_SHIPMENT_DATES);
        }

        offers.add(
                createOffer(
                        counterPrice,
                        newVehicleType,
                        counterVehicleNumber,
                        loadingDate,
                        deliveryDate,
                        reason,
                        consignorId
                )
        );

        recordStatus(ShipmentStatus.CONSIGNOR_COUNTERED, consignorId, reason);
    }

    public void consignorRejectsOffer(String reason) {
        assertNegotiable();
        assertLatestOfferExists();

        if(currentStatus.isRejected()){
            throw new BusinessException(ErrorCode.SHIPMENT_OFFER_ALREADY_REJECTED);
        }

        if(currentStatus == ShipmentStatus.ADMIN_REQUESTED_CHANGE){
            throw new BusinessException(ErrorCode.OPERATION_NOT_ALLOWED_IN_CURRENT_STATE);
        }

        ShipmentOffer latestOffer = latestOffer();
        if(latestOffer.getOfferedBy().equals(consignorId)){
            throw new BusinessException(ErrorCode.OPERATION_NOT_ALLOWED_IN_CURRENT_STATE);
        }

        recordStatus(
                ShipmentStatus.CONSIGNOR_REJECTED_OFFER,
                consignorId,
                reason
        );
    }

    // Consignor Accepts
    public void consignorAccepts(String reason) {
        assertNegotiable();
        assertLatestOfferExists();

        if (currentStatus == ShipmentStatus.CONSIGNOR_ACCEPTED ||
                currentStatus == ShipmentStatus.ADMIN_APPROVED) {
            throw new BusinessException(ErrorCode.SHIPMENT_ALREADY_ACCEPTED);
        }

        if(currentStatus == ShipmentStatus.ADMIN_REQUESTED_CHANGE){
            throw new BusinessException(ErrorCode.OPERATION_NOT_ALLOWED_IN_CURRENT_STATE);
        }

        ShipmentOffer latest = latestOffer();
        if(latest.getOfferedBy().equals(consignorId)){
            throw new BusinessException(ErrorCode.OPERATION_NOT_ALLOWED_IN_CURRENT_STATE);
        }

        applyOffer(latest);

        recordStatus(ShipmentStatus.CONSIGNOR_ACCEPTED, consignorId, reason);
    }

    // admin approve
    public void approve(UUID adminId) {

        assertNegotiable();
        if(currentStatus.isRejected()){
            throw new BusinessException(ErrorCode.SHIPMENT_NON_NEGOTIABLE);
        }

        boolean consignorAccepted =
                currentStatus == ShipmentStatus.CONSIGNOR_ACCEPTED;

        boolean adminAgreesWithConsignorOffer =
                currentStatus != ShipmentStatus.CONSIGNOR_ACCEPTED
                        && lastOfferMadeByConsignor();

        if (!consignorAccepted && !adminAgreesWithConsignorOffer) {
            throw new BusinessException(ErrorCode.SHIPMENT_NOT_APPROVABLE);
        }

        // If approval is based on consignor's last offer,
        // apply it now, otherwise it is already applied when consignor accepts
        if (adminAgreesWithConsignorOffer) {
            ShipmentOffer latest = latestOffer();
            applyOffer(latest);
        }

        recordStatus(ShipmentStatus.ADMIN_APPROVED, adminId, "Shipment approved");
    }

    // mark driver assigned
    public void markDriverAssigned(UUID driverId) {
        if (currentStatus != ShipmentStatus.ADMIN_APPROVED) {
            throw new BusinessException(ErrorCode.OPERATION_NOT_ALLOWED_IN_CURRENT_STATE);
        }
        recordStatus(ShipmentStatus.DRIVER_ASSIGNED, driverId, "Driver assigned");
    }

    // mark in progress
    public void markInProgress(UUID systemActor) {
        if (currentStatus != ShipmentStatus.ADMIN_APPROVED
                && currentStatus != ShipmentStatus.DRIVER_ASSIGNED
        ) {
            throw new BusinessException(ErrorCode.SHIPMENT_NOT_APPROVABLE);
        }

        recordStatus(ShipmentStatus.IN_PROGRESS, systemActor, "Execution started");
    }

    // mark completed
    public void markCompleted(UUID systemActor) {
        if (currentStatus != ShipmentStatus.IN_PROGRESS) {
            throw new BusinessException(ErrorCode.OPERATION_NOT_ALLOWED_IN_CURRENT_STATE);
        }

        recordStatus(ShipmentStatus.COMPLETED, systemActor, "Shipment completed");
    }

    // canceled
    public void cancelByConsignor(String reason) {
        assertNegotiable();

        recordStatus(
                ShipmentStatus.CANCELLED_BY_CONSIGNOR,
                consignorId,
                reason
        );
    }

    public void cancelByAdmin(UUID adminId, String reason) {
        if (currentStatus.isTerminal()) {
            throw new BusinessException(ErrorCode.SHIPMENT_CANCELLED);
        }

        recordStatus(
                ShipmentStatus.CANCELLED_BY_ADMIN,
                adminId,
                reason
        );
    }

    //Negotiation helpers
    private void assertNegotiable() {
        if (currentStatus == ShipmentStatus.IN_PROGRESS ||
                currentStatus == ShipmentStatus.COMPLETED ||
                currentStatus == ShipmentStatus.CANCELLED_SYSTEM ||
                currentStatus == ShipmentStatus.CANCELLED_BY_ADMIN ||
                currentStatus == ShipmentStatus.CANCELLED_BY_CONSIGNOR) {
            throw new BusinessException(ErrorCode.SHIPMENT_NON_NEGOTIABLE);
        }
    }

    private void assertLatestOfferExists(){
        if(this.offers.isEmpty()){
            throw new BusinessException(ErrorCode.OFFER_NOT_FOUND);
        }
    }

    private ShipmentOffer createOffer(
            BigDecimal priceAmount,
            String vehicleType,
            int vehicleNumber,
            LocalDateTime loadingDate,
            LocalDateTime deliveryDate,
            String reason,
            UUID actor
    ) {

        return new ShipmentOffer(
                nextRound(),
                priceAmount,
                vehicleType,
                vehicleNumber,
                loadingDate,
                deliveryDate,
                reason,
                actor
        );
    }

    // status recording
    private void recordStatus(
            ShipmentStatus status,
            UUID actor,
            String reason
    ) {
        this.statusHistory.add(
                new ShipmentStatusHistory(
                        status,
                        actor,
                        reason,
                        LocalDateTime.now()
                )
        );
        this.currentStatus = status;
        this.updatedAt = LocalDateTime.now();
    }

    private int nextRound() {
        return offers.size() + 1;
    }

    private ShipmentOffer latestOffer() {
        return offers.stream()
                .max(Comparator.comparingInt(ShipmentOffer::getRound))
                .orElseThrow(() -> new BusinessException(ErrorCode.OFFER_NOT_FOUND));
    }

    private boolean lastOfferMadeByConsignor() {
        return latestOffer().getOfferedBy().equals(consignorId);
    }

    private void applyOffer(ShipmentOffer latest){
        this.priceAmount = latest.getPriceAmount();
        this.requiredVehicleNumber = latest.getRequiredVehicleNumber();
        this.requiredVehicleType = latest.getRequiredVehicleType();
        this.deliveryDate = latest.getDeliveryDate();
        this.loadingDate = latest.getLoadingDate();
    }


}

