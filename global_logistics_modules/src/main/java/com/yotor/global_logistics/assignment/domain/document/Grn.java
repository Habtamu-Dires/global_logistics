package com.yotor.global_logistics.assignment.domain.document;

import com.yotor.global_logistics.assignment.application.document.dto.CreateGrnRequest;
import com.yotor.global_logistics.assignment.domain.assignment.ShipmentAssignment;
import com.yotor.global_logistics.assignment.domain.document.dto.GrnStatus;
import com.yotor.global_logistics.exception.BusinessException;
import com.yotor.global_logistics.exception.ErrorCode;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Table("grn")
public class Grn {

    @Id
    private Long id;

    private UUID publicId;
    private String grnNumber;

    private UUID assignmentId;
    private UUID shipmentId;

    private Long gdnId;

    private Integer receivedQuantity;
    private String receivedWeight;
    private String receivedVolume;

    private Integer damageQuantity;
    private Integer shortageQuantity;

    private String conditionNote;

    private UUID receivedBy;
    private LocalDateTime receivedAt;

    private GrnStatus status;

    private UUID voidedBy;
    private LocalDateTime voidedAt;
    private String voidReason;

    private LocalDateTime createdAt;


    /** --domain rule */
    private Grn(){}

    public static Grn generate(
            ShipmentAssignment assignment,
            Gdn gdn,
            String grnNumber,
            UUID receiverId,
            CreateGrnRequest data
    ) {
        gdn.ensureActive();

        Grn grn = new Grn();

        grn.publicId = UUID.randomUUID();
        grn.grnNumber = grnNumber;

        grn.assignmentId = assignment.getPublicId();
        grn.shipmentId = assignment.getShipmentId();

        grn.gdnId = gdn.getId();

        grn.receivedQuantity = data.receivedQuantity();
        grn.receivedWeight = data.receivedWeight();
        grn.receivedVolume = data.receivedVolume();

        grn.damageQuantity = data.damageQuantity();
        grn.shortageQuantity = data.shortageQuantity();
        grn.conditionNote = data.conditionNote();

        grn.receivedBy = receiverId;
        grn.receivedAt = LocalDateTime.now();

        grn.status = GrnStatus.GENERATED;
        grn.createdAt = LocalDateTime.now();

        return grn;
    }

    public void voidDocument(UUID adminId, String reason) {
        if (status == GrnStatus.VOID) {
            throw new BusinessException(ErrorCode.OPERATION_NOT_ALLOWED_IN_CURRENT_STATE);
        }

        this.status = GrnStatus.VOID;
        this.voidedBy = adminId;
        this.voidedAt = LocalDateTime.now();
        this.voidReason = reason;
    }


}
