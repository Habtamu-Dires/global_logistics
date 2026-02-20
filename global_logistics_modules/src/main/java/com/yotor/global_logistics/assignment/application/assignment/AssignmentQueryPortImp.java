package com.yotor.global_logistics.assignment.application.assignment;

import com.yotor.global_logistics.assignment.api.AssignmentQueryPort;
import com.yotor.global_logistics.assignment.api.dto.AssignmentTrackingRes;
import com.yotor.global_logistics.assignment.domain.assignment.dto.AssignmentStatus;
import com.yotor.global_logistics.assignment.persistence.AssignmentRepository;
import com.yotor.global_logistics.exception.BusinessException;
import com.yotor.global_logistics.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AssignmentQueryPortImp implements AssignmentQueryPort {

    private final AssignmentRepository assignmentRepo;

    @Override
    public AssignmentTrackingRes findByExternalIdForTracking(UUID assignmentId) {
        return assignmentRepo.findByPublicIdForTracking(assignmentId)
                .orElseThrow(()-> new BusinessException(ErrorCode.ASSIGNMENT_NOT_FOUND));
    }

    @Override
    public UUID getShipmentId(UUID assignmentId) {
        return assignmentRepo.findShipmentId(assignmentId);
    }

    @Override
    public boolean isAssignmentCompleted(UUID assignmentPublicId) {
        return assignmentRepo.findByPublicId(assignmentPublicId)
                .map(a -> a.getStatus() == AssignmentStatus.CONSIGNOR_CONFIRMED)
                .orElse(false);
    }

    @Override
    public UUID getDriverId(UUID assignmentId) {
        return assignmentRepo.findDriverId(assignmentId)
                .orElseThrow(()-> new BusinessException(ErrorCode.ASSIGNMENT_NOT_FOUND));
    }

}
