package com.yotor.global_logistics.driver_negotiation.domain;

import com.yotor.global_logistics.driver_negotiation.domain.dto.NegotiationStatus;
import com.yotor.global_logistics.exception.BusinessException;
import com.yotor.global_logistics.exception.ErrorCode;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Getter
@Table("driver_negotiation")
public class DriverNegotiation {

    @Id
    private Long id;

    private UUID publicId;

    private UUID shipmentId;
    private UUID driverId;

    private NegotiationStatus status;

    private BigDecimal finalAgreedPrice;

    private String startLocation;
    private Double startLatitude;
    private Double startLongitude;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    @MappedCollection(idColumn = "negotiation_id")
    private Set<DriverOffer> offers = new HashSet<>();

    @PersistenceCreator
    public DriverNegotiation(
            Long id,
            UUID publicId,

            UUID shipmentId,
            UUID driverId,

            NegotiationStatus status,
            BigDecimal finalAgreedPrice,

            String startLocation,
            Double startLatitude,
            Double startLongitude,

            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.id = id;
        this.publicId = publicId;

        this.shipmentId = shipmentId;
        this.driverId = driverId;

        this.status = status;
        this.finalAgreedPrice = finalAgreedPrice;

        this.startLocation = startLocation;
        this.startLatitude = startLatitude;
        this.startLongitude = startLongitude;

        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    private DriverNegotiation(
            UUID shipmentId,
            UUID driverId,
            BigDecimal price
    ){
        this.publicId = UUID.randomUUID();
        this.shipmentId = shipmentId;
        this.driverId = driverId;
        this.finalAgreedPrice = price;
        this.status = NegotiationStatus.CREATED;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /** --Domain Rule **/

    private static DriverNegotiation create(
            UUID shipmentId,
            UUID driverId,
            BigDecimal price
    ){

        return new DriverNegotiation(
               shipmentId,
                driverId,
                price
        );

    }

    // admin initial offer
    public static DriverNegotiation adminSendInitialOffer(
            UUID shipmentId, BigDecimal price, UUID driverId, String reason
    ) {

        DriverNegotiation negotiation = create(shipmentId, driverId, price);
        negotiation.status = NegotiationStatus.OFFER_SENT;
        negotiation.addOffer(price, driverId, reason);
        return negotiation;
    }

    // admin counter
    public void adminCounter(BigDecimal price, UUID adminId, String reason) {
        assertNegotiable();

        addOffer(price, adminId, reason);
        status = NegotiationStatus.ADMIN_COUNTERED;
    }

    public void driverCounter(BigDecimal price, UUID driverId, String reason) {
        assertNegotiable();
        if (status != NegotiationStatus.OFFER_SENT &&
                status != NegotiationStatus.ADMIN_COUNTERED) {
            throw new BusinessException(ErrorCode.OPERATION_NOT_ALLOWED_IN_CURRENT_STATE);
        }

        addOffer(price, driverId, reason);

        status = NegotiationStatus.DRIVER_COUNTERED;
    }

    public void driverAccept(
            Double lat,
            Double lon,
            String locationText
    ) {

        assertLatestOfferExists();

        DriverOffer latest = latestOffer();
        if(latest.getOfferedBy() == driverId){
            throw new BusinessException(ErrorCode.OPERATION_NOT_ALLOWED_IN_CURRENT_STATE);
        }

        if(status != NegotiationStatus.ADMIN_COUNTERED &&
                status != NegotiationStatus.OFFER_SENT
        ){
            throw new BusinessException(ErrorCode.OPERATION_NOT_ALLOWED_IN_CURRENT_STATE);
        }

        // apply offer
        applyOffer(latest, lat, lon, locationText);

        this.status = NegotiationStatus.DRIVER_ACCEPTS;
    }

    public void driverReject(String reason){
        assertNegotiable();
       assertLatestOfferExists();

        DriverOffer latestOffer = latestOffer();
        if(latestOffer.getOfferedBy() == driverId){
            throw new BusinessException(ErrorCode.OPERATION_NOT_ALLOWED_IN_CURRENT_STATE);
        }
        if(status != NegotiationStatus.ADMIN_COUNTERED &&
            status != NegotiationStatus.OFFER_SENT ){
            throw new BusinessException(ErrorCode.OPERATION_NOT_ALLOWED_IN_CURRENT_STATE);
        }
        this.status = NegotiationStatus.DRIVER_REJECTS;
    }

    public void driverCancel(String reason){
        assertNegotiable();
        assertLatestOfferExists();

        if(status != NegotiationStatus.SELECTED){
            throw new BusinessException(ErrorCode.OPERATION_NOT_ALLOWED_IN_CURRENT_STATE);
        }

        this.status = NegotiationStatus.DRIVER_CANCEL;
    }

    public void adminCancel(String reason){
        assertNegotiable();
        assertLatestOfferExists();

//        if(status != NegotiationStatus.SELECTED){
//            throw new BusinessException(ErrorCode.OPERATION_NOT_ALLOWED_IN_CURRENT_STATE);
//        }

        this.status = NegotiationStatus.CANCELED;

    }

    public void markSelected() {

        assertNegotiable();
        assertLatestOfferExists();

        if (status != NegotiationStatus.DRIVER_ACCEPTS) {
            throw new BusinessException(ErrorCode.OPERATION_NOT_ALLOWED_IN_CURRENT_STATE);
        }

        this.status = NegotiationStatus.SELECTED;
    }


    public void markNotSelected(){
        assertNegotiable();
        assertLatestOfferExists();

        if (status != NegotiationStatus.DRIVER_ACCEPTS) {
            throw new BusinessException(ErrorCode.OPERATION_NOT_ALLOWED_IN_CURRENT_STATE);
        }

        this.status = NegotiationStatus.NOT_SELECTED;
    }

    public void markExpired(){
        assertNegotiable();
        assertLatestOfferExists();

        if (status == NegotiationStatus.DRIVER_ACCEPTS) {
            throw new BusinessException(ErrorCode.OPERATION_NOT_ALLOWED_IN_CURRENT_STATE);
        }

        this.status = NegotiationStatus.EXPIRED;
    }

    // add offer
    private void addOffer(BigDecimal price, UUID offeredBy, String reason) {
         offers.add(
                 createOffer(
                         price,
                         offeredBy,
                         reason
                 )
         );
    }

    // create offer
    public DriverOffer createOffer(BigDecimal price, UUID offeredBy, String reason){
        return new DriverOffer(
                nextRound(),
                price,
                offeredBy,
                reason
        );
    }

    private int nextRound() {
        return offers.size() + 1;
    }

    private DriverOffer latestOffer() {
        return offers.stream()
                .max(Comparator.comparingInt(DriverOffer::getRound))
                .orElseThrow(() -> new BusinessException(ErrorCode.OFFER_NOT_FOUND));
    }

    private void assertLatestOfferExists(){
        if(this.offers.isEmpty()){
            throw new BusinessException(ErrorCode.OFFER_NOT_FOUND);
        }
    }

    public void assertNegotiable(){
        if(status == NegotiationStatus.CANCELED
            || status == NegotiationStatus.DRIVER_CANCEL
            || status == NegotiationStatus.EXPIRED
        ){
            throw new BusinessException(ErrorCode.SHIPMENT_NON_NEGOTIABLE);
        }
    }

    private void applyOffer(DriverOffer latest, Double lat, Double lon, String locationText){
        this.finalAgreedPrice = latest.getPriceAmount();
        this.startLatitude = lat;
        this.startLongitude = lon;
        this.startLocation = locationText;
    }


}

