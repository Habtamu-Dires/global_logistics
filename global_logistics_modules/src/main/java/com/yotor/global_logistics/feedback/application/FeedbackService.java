package com.yotor.global_logistics.feedback.application;

import com.yotor.global_logistics.assignment.api.AssignmentQueryPort;
import com.yotor.global_logistics.exception.BusinessException;
import com.yotor.global_logistics.exception.ErrorCode;
import com.yotor.global_logistics.feedback.application.dto.CreateFeedbackRequest;
import com.yotor.global_logistics.feedback.domain.Feedback;
import com.yotor.global_logistics.feedback.domain.FeedbackActorType;
import com.yotor.global_logistics.feedback.domain.FeedbackTargetType;
import com.yotor.global_logistics.feedback.persistence.FeedbackRepository;
import com.yotor.global_logistics.security.SecurityUtils;
import com.yotor.global_logistics.shipment.port.ShipmentQueryPort;
import io.swagger.v3.oas.annotations.servers.Server;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Server
@RequiredArgsConstructor
@Transactional
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final AssignmentQueryPort assignmentQueryPort;
    private final ShipmentQueryPort shipmentQueryPort;

    public UUID giveFeedbackToDriver(CreateFeedbackRequest req) {

        UUID actorId = SecurityUtils.currentUser().userPublicId();

        validateAssignmentCompleted(req.assignmentId());

        UUID driverId =
                assignmentQueryPort.getDriverId(req.assignmentId());

        ensureNotAlreadyGiven(
                req.assignmentId(),
                FeedbackActorType.CONSIGNOR,
                FeedbackTargetType.DRIVER
        );

        Feedback feedback = Feedback.create(
                req.assignmentId(),
                FeedbackActorType.CONSIGNOR,
                actorId,
                FeedbackTargetType.DRIVER,
                driverId,
                req.rating(),
                req.comment()
        );

        feedbackRepository.save(feedback);

        return feedback.getPublicId();
    }

    public UUID giveFeedbackToConsignor(CreateFeedbackRequest req) {

        UUID actorId = SecurityUtils.currentUser().userPublicId();

        validateAssignmentCompleted(req.assignmentId());

        UUID shipmentId =
                assignmentQueryPort.getShipmentId(req.assignmentId());
        UUID consignorId = shipmentQueryPort.getConsignorId(shipmentId);

        ensureNotAlreadyGiven(
                req.assignmentId(),
                FeedbackActorType.DRIVER,
                FeedbackTargetType.CONSIGNOR
        );

        Feedback feedback = Feedback.create(
                req.assignmentId(),
                FeedbackActorType.DRIVER,
                actorId,
                FeedbackTargetType.CONSIGNOR,
                consignorId,
                req.rating(),
                req.comment()
        );

        feedbackRepository.save(feedback);

        return feedback.getPublicId();
    }

    public UUID givePlatformFeedback(CreateFeedbackRequest req) {

        UUID actorId = SecurityUtils.currentUser().userPublicId();
        List<String> roles = SecurityUtils.currentUser().roles().stream().toList();
        FeedbackActorType actorType =
                FeedbackActorType.valueOf(roles.getFirst());

        validateAssignmentCompleted(req.assignmentId());

        ensureNotAlreadyGiven(
                req.assignmentId(),
                actorType,
                FeedbackTargetType.PLATFORM
        );

        Feedback feedback = Feedback.create(
                req.assignmentId(),
                actorType,
                actorId,
                FeedbackTargetType.PLATFORM,
                null,
                req.rating(),
                req.comment()
        );

        feedbackRepository.save(feedback);

        return feedback.getPublicId();
    }

    private void validateAssignmentCompleted(UUID assignmentId) {
        if (!assignmentQueryPort.isAssignmentCompleted(assignmentId)) {
            throw new BusinessException(ErrorCode.OPERATION_NOT_ALLOWED_IN_CURRENT_STATE);
        }
    }

    private void ensureNotAlreadyGiven(
            UUID assignmentId,
            FeedbackActorType actor,
            FeedbackTargetType target
    ) {
        if (feedbackRepository
                .existsByAssignmentIdAndGivenByActorTypeAndTargetActorType(
                        assignmentId, actor, target
                )) {

            throw new BusinessException(ErrorCode.FEEDBACK_ALREADY_GIVEN);
        }
    }


}
