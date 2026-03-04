package com.yotor.global_logistics.assignment.application.document;

import com.yotor.global_logistics.assignment.application.document.dto.CreateGrnRequest;
import com.yotor.global_logistics.assignment.application.document.dto.DocumentType;
import com.yotor.global_logistics.assignment.domain.assignment.ShipmentAssignment;
import com.yotor.global_logistics.assignment.domain.assignment.dto.ActorType;
import com.yotor.global_logistics.assignment.domain.document.Gdn;
import com.yotor.global_logistics.assignment.domain.document.Grn;
import com.yotor.global_logistics.assignment.persistence.AssignmentRepository;
import com.yotor.global_logistics.assignment.persistence.GdnRepository;
import com.yotor.global_logistics.assignment.persistence.GrnRepository;
import com.yotor.global_logistics.exception.BusinessException;
import com.yotor.global_logistics.exception.ErrorCode;
import com.yotor.global_logistics.security.SecurityUtils;
import com.yotor.global_logistics.assignment_finance.api.AssignmentFinancePort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GrnService {

    private final GrnRepository grnRepo;
    private final AssignmentRepository assignmentRepo;
    private final DocumentNumberGenerator numberGenerator;
    private final GdnRepository gdnRepo;
    private final AssignmentFinancePort assignmentFinancePort;

    @PreAuthorize("hasAnyRole('ADMIN','CONSIGNOR')")
    @Transactional
    public UUID generateGrn(UUID assignmentPublicId, CreateGrnRequest req){
        UUID actorId = SecurityUtils.currentUser().userPublicId();
        List<String> roles = SecurityUtils.currentUser().roles().stream().toList();
        ShipmentAssignment assignment =
                assignmentRepo.findByPublicId(assignmentPublicId)
                        .orElseThrow();

        Gdn gdn = gdnRepo
                .findActiveGdnByAssignmentId(assignment.getId())
                .orElseThrow(()-> new BusinessException(ErrorCode.OPERATION_NOT_ALLOWED_IN_CURRENT_STATE));


        String docNumber = numberGenerator.next(DocumentType.GRN);

        Grn grn = Grn.generate(
                assignment,
                gdn,
                docNumber,
                actorId,
                req
        );

        grnRepo.save(grn);
        assignment.markGrnGenerated(actorId, ActorType.valueOf(roles.getFirst()));
        assignmentFinancePort.createFinanceForAssignment(
                assignmentPublicId,
                assignment.getAgreedPrice()
        );

        return grn.getPublicId();
    }
}
