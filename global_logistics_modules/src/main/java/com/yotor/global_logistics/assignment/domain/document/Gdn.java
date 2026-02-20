package com.yotor.global_logistics.assignment.domain.document;


import com.yotor.global_logistics.assignment.application.document.dto.CreateGdnRequest;
import com.yotor.global_logistics.assignment.domain.document.dto.AssignmentSnapshot;
import com.yotor.global_logistics.assignment.domain.document.dto.GdnStatus;
import com.yotor.global_logistics.assignment.domain.document.dto.GrnStatus;
import com.yotor.global_logistics.exception.BusinessException;
import com.yotor.global_logistics.exception.ErrorCode;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Table("gdn")
@Getter
public class Gdn {

    @Id
    private Long id;

    private UUID publicId;
    private String documentNumber;

    private Long assignmentId;
    private UUID shipmentId;

    private GdnStatus status;

    private UUID issuedBy;
    private LocalDateTime issuedAt;

    // --- Consignor snapshot ---
    private String consignorName;
    private String consignorContact;

    // consignee snapshot
    private String consigneeName;
    private String consigneeContact;

    // --- Driver snapshot ---
    private String driverName;
    private String driverPhone;

    // --- Vehicle snapshot ---
    private String vehicleType;
    private String vehiclePlateNo;

    // --- Goods snapshot ---
    private String goodsType;
    private String unitOfMeasure;
    private Integer quantity;
    private String weight;
    private String volume;
    private String goodsDescription;

    private String packagingType;

    // --- logistics snapshot ---
    private String loadingLocation;
    private LocalDateTime loadingDate;
    private String offloadingLocation;

    // operational note
    private String remarks;

    //verification
    private String qrCodeValue;

    // audit
    private UUID voidedBy;
    private String voidReason;
    private LocalDateTime voidedAt;

    private Gdn(){}

    @PersistenceCreator
    public Gdn(
            Long id,
            UUID publicId,
            String documentNumber,
            Long assignmentId,
            UUID shipmentId,

            String status,

            UUID issuedBy, LocalDateTime issuedAt,

            String consignorName, String consignorContact,
            String consigneeName, String consigneeContact,

            String driverName, String driverPhone,

            String vehicleType, String vehiclePlateNumber,

            String goodsType,

            Integer quantity, String weight, String volume,
            String goodsDescription, String packagingType,

            String loadingLocation, LocalDateTime loadingDate, String offloadingLocation,

            String remarks,
            String qrCodeValue,

            UUID voidedBy,String voidReason, LocalDateTime voidedAt
    ) {
        this.id = id;
        this.publicId = publicId;
        this.documentNumber = documentNumber;
        this.assignmentId = assignmentId;
        this.shipmentId = shipmentId;
        this.status = GdnStatus.valueOf(status);
        this.issuedBy = issuedBy;
        this.issuedAt = issuedAt;
        this.consignorName = consignorName;
        this.consignorContact = consignorContact;
        this.consigneeName = consigneeName;
        this.consigneeContact = consigneeContact;
        this.driverName = driverName;
        this.driverPhone = driverPhone;
        this.vehicleType = vehicleType;
        this.vehiclePlateNo = vehiclePlateNumber;
        this.goodsType = goodsType;
        this.quantity = quantity;
        this.weight = weight;
        this.volume = volume;
        this.goodsDescription = goodsDescription;
        this.packagingType = packagingType;
        this.loadingLocation = loadingLocation;
        this.loadingDate = loadingDate;
        this.offloadingLocation = offloadingLocation;
        this.remarks = remarks;
        this.qrCodeValue = qrCodeValue;
        this.voidedBy = voidedBy;
        this.voidReason = voidReason;
        this.voidedAt = voidedAt;

    }

    public static Gdn generate(
            String documentNumber,
            Long assignmentId,
            UUID shipmentId,
            UUID adminId,
            AssignmentSnapshot snapshot,
            CreateGdnRequest req
    ) {
        Gdn gdn = new Gdn();

        gdn.publicId = UUID.randomUUID();
        gdn.documentNumber = documentNumber;

        gdn.assignmentId = assignmentId;
        gdn.shipmentId = shipmentId;

        gdn.status = GdnStatus.DRAFT;

        gdn.issuedBy = adminId;
        gdn.issuedAt = LocalDateTime.now();

        gdn.consignorName = snapshot.consignorName();
        gdn.consignorContact = snapshot.consignorPhone();

        gdn.consigneeName = req.consigneeName();
        gdn.consigneeContact = req.consigneeContact();

        gdn.driverName = snapshot.driverName();
        gdn.driverPhone = snapshot.driverPhone();

        gdn.vehicleType = snapshot.vehicleType();
        gdn.vehiclePlateNo = snapshot.vehiclePlateNo();

        gdn.goodsType = snapshot.goodsType();
        gdn.goodsDescription = snapshot.goodsDescription();
        gdn.quantity = req.quantity();
        gdn.weight = req.weight();
        gdn.volume = req.volume();
        gdn.packagingType = req.packagingType();

        gdn.loadingLocation = snapshot.loadingLocation();
        gdn.loadingDate = snapshot.loadingDate();
        gdn.offloadingLocation = snapshot.offloadingLocation();

        return gdn;
    }

    public void voidDocument(UUID adminId, String reason) {
        if (status == GdnStatus.VOID) {
            throw new BusinessException(ErrorCode.OPERATION_NOT_ALLOWED_IN_CURRENT_STATE);
        }

        this.status = GdnStatus.VOID;
        this.voidedBy = adminId;
        this.voidedAt = LocalDateTime.now();
        this.voidReason = reason;
    }

    public void ensureActive() {
        if (status != GdnStatus.ISSUED) {
            throw new BusinessException(ErrorCode.OPERATION_NOT_ALLOWED_IN_CURRENT_STATE);
        }
    }
}

