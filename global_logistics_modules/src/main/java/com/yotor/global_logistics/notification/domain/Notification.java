package com.yotor.global_logistics.notification.domain;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Table("notification")
@Getter
public class Notification {

    @Id
    private Long id;

    private UUID publicId;

    private UUID receiverId;
    private NotificationActorType receiverType;

    private String title;
    private String message;

    private NotificationType type;

    private NotificationReferenceType referenceType;
    private UUID referenceId;

    private boolean isRead;
    private LocalDateTime readAt;

    private LocalDateTime createdAt;

    // constructors
    Notification(){}
    @PersistenceCreator
    public Notification(
            Long id,
            UUID publicId,
            UUID receiverId,
            String receiverType,
            String title,
            String message,
            String type,
            String referenceType,
            UUID referenceId,
            boolean isRead,
            LocalDateTime readAt,
            LocalDateTime createdAt
    ) {
        this.id = id;
        this.publicId = publicId;
        this.receiverId = receiverId;
        this.receiverType = NotificationActorType.valueOf(receiverType);
        this.title = title;
        this.message = message;
        this.type = NotificationType.valueOf(type);
        this.referenceType = referenceType != null ? NotificationReferenceType.valueOf(referenceType) : null;
        this.referenceId = referenceId;
        this.isRead = isRead;
        this.readAt = readAt;
        this.createdAt = createdAt;
    }

    public static Notification create(
            UUID receiverId,
            NotificationActorType receiverType,
            String title,
            String message,
            NotificationType type,
            NotificationReferenceType referenceType,
            UUID referenceId
    ) {
        Notification notification = new Notification();
        notification.publicId = UUID.randomUUID();
        notification.receiverId = receiverId;
        notification.receiverType = receiverType;
        notification.title = title;
        notification.message = message;
        notification.type = type;
        notification.referenceType = referenceType;
        notification.referenceId = referenceId;
        return notification;
    }

    public void markAsRead() {
        if (!this.isRead) {
            this.isRead = true;
            this.readAt = LocalDateTime.now();
        }
    }
}