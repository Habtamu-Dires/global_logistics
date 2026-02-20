package com.yotor.global_logistics.notification.application;

import com.yotor.global_logistics.assignment.event.AssignmentCreatedEvent;
import com.yotor.global_logistics.notification.domain.Notification;
import com.yotor.global_logistics.notification.domain.NotificationActorType;
import com.yotor.global_logistics.notification.domain.NotificationReferenceType;
import com.yotor.global_logistics.notification.domain.NotificationType;
import com.yotor.global_logistics.notification.persistence.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class AssignmentNotificationListener {

    private final NotificationRepository notificationRepository;

    @Async
    @TransactionalEventListener
    public void onAssignmentCreated(AssignmentCreatedEvent event) {

        Notification notification =
                Notification.create(
                      event.driverId(),
                      NotificationActorType.DRIVER,
                      "Assigned",
                      "You have been assigned for shipment",
                      NotificationType.DRIVER_ASSIGNED,
                      NotificationReferenceType.ASSIGNMENT,
                      event.assignmentPublicId()
                );

        notificationRepository.save(notification);
    }
}
