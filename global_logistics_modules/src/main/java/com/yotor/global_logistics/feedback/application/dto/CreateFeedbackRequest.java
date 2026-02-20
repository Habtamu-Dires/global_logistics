package com.yotor.global_logistics.feedback.application.dto;

import java.util.UUID;

public record CreateFeedbackRequest(
        UUID assignmentId,
        String comment,
        int rating
) {
}
