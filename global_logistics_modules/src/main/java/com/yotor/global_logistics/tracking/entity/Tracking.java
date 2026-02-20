package com.yotor.global_logistics.tracking.entity;

import lombok.Getter;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Table(name = "tracking")
public class Tracking {

    private final Long id;

    private final UUID assignmentId;

    private final Double latitude;
    private final Double longitude;

    private final Double accuracy;
    private final Double speed;

    private final LocalDateTime recordedAt;
    private final LocalDateTime receivedAt;


    @PersistenceCreator
    public Tracking(
            Long id,
            UUID assignmentId,
            Double latitude,
            Double longitude,
            Double accuracy,
            Double speed,
            LocalDateTime recordedAt,
            LocalDateTime receivedAt
    ){
        this.id = id;
        this.assignmentId = assignmentId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.accuracy = accuracy;
        this.speed = speed;
        this.recordedAt = recordedAt;
        this.receivedAt = receivedAt;
    }

    public static Tracking create(
            UUID assignmentId,
            Double latitude,
            Double longitude,
            Double accuracy,
            Double speed,
            LocalDateTime recordedAt
    ){
        return new Tracking(null,
                assignmentId,
                latitude,
                longitude,
                accuracy,
                speed,
                recordedAt,
                LocalDateTime.now()
        );
    }

}
