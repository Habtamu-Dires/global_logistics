package com.yotor.global_logistics.feedback.domain;

import com.yotor.global_logistics.exception.BusinessException;
import com.yotor.global_logistics.exception.ErrorCode;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Table("feedback")
public class Feedback {

    @Id
    private Long id;

    private UUID publicId;
    private UUID assignmentId;

    private FeedbackActorType givenByActorType;
    private UUID givenByActorId;

    private FeedbackTargetType targetActorType;
    private UUID targetActorId;

    private Integer rating;
    private String comment;

    private LocalDateTime createdAt;


    // constructors
    public Feedback(){}

    @PersistenceCreator
    public Feedback(
            Long id,
            UUID assignmentId,
            FeedbackActorType givenByActorType,
            UUID givenByActorId,
            FeedbackTargetType targetActorType,
            UUID targetActorId,
            Integer rating,
            String comment,
            LocalDateTime createdAt
    ){
        this.id = id;
        this.publicId = UUID.randomUUID();
        this.assignmentId = assignmentId;
        this.givenByActorType = givenByActorType;
        this.givenByActorId = givenByActorId;
        this.targetActorType = targetActorType;
        this.targetActorId = targetActorId;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = createdAt;
    }

    public static Feedback create(
            UUID assignmentId,
            FeedbackActorType givenByType,
            UUID givenById,
            FeedbackTargetType targetType,
            UUID targetId,
            Integer rating,
            String comment
    ) {

        validateRating(rating);
        Feedback feedback = new Feedback();
        feedback.publicId = UUID.randomUUID();
        feedback.assignmentId = assignmentId;
        feedback.givenByActorType = givenByType;
        feedback.givenByActorId = givenById;
        feedback.targetActorType = targetType;
        feedback.targetActorId = targetId;
        feedback.rating = rating;
        feedback.comment = comment;
        feedback.createdAt = LocalDateTime.now();

        return feedback;
    }

    private static void validateRating(Integer rating) {
        if (rating != null && (rating < 1 || rating > 5)) {
            throw new BusinessException(ErrorCode.INVALID_RATING);
        }
    }


}
