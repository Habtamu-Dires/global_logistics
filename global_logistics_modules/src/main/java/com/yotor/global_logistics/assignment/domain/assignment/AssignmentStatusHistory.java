package com.yotor.global_logistics.assignment.domain.assignment;

import com.yotor.global_logistics.assignment.domain.assignment.dto.ActorType;
import com.yotor.global_logistics.assignment.domain.assignment.dto.AssignmentStatus;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Table("assignment_status_history")
public class AssignmentStatusHistory {

    @Id
    private Long id;

    private AssignmentStatus fromStatus;
    private AssignmentStatus toStatus;

    private UUID changedBy;
    private ActorType actorType;

    private String remark;
    private LocalDateTime changedAt;

    public static AssignmentStatusHistory record(
            AssignmentStatus from,
            AssignmentStatus to,
            UUID actorId,
            ActorType actorType,
            String remark
    ) {
        AssignmentStatusHistory h = new AssignmentStatusHistory();
            h.fromStatus = from;
            h.toStatus = to;
            h.changedBy = actorId;
            h.actorType = actorType;
            h.remark = remark;
            h.changedAt = LocalDateTime.now();
            return h;
    }
}

