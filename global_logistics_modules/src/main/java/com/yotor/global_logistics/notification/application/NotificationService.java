package com.yotor.global_logistics.notification.application;

import com.yotor.global_logistics.exception.BusinessException;
import com.yotor.global_logistics.exception.ErrorCode;
import com.yotor.global_logistics.notification.application.dto.NotificationResponse;
import com.yotor.global_logistics.notification.application.dto.UnreadCountResponse;
import com.yotor.global_logistics.notification.domain.Notification;
import com.yotor.global_logistics.notification.persistence.NotificationRepository;
import com.yotor.global_logistics.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepo;

    @Transactional(readOnly = true)
    public List<NotificationResponse> getLatestForCurrentUser() {

        UUID userId = SecurityUtils.currentUser().userPublicId();

        return notificationRepo.findLatest(userId, 20)
                .stream()
                .map(NotificationResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public long unreadCount() {
        UUID userId = SecurityUtils.currentUser().userPublicId();
        return notificationRepo.countUnread(userId);
    }

    @Transactional(readOnly = true)
    public List<UnreadCountResponse> unreadByReferenceType() {

        UUID userId = SecurityUtils.currentUser().userPublicId();

        return notificationRepo.countUnreadGroupByReference(userId)
                .stream()
                .map(UnreadCountResponse::from)
                .toList();
    }

    @Transactional
    public void markAsRead(UUID publicId) {

        Notification notification =
                notificationRepo.findByPublicId(publicId)
                        .orElseThrow(()-> new BusinessException(ErrorCode.NOTIFICATION_NOT_FOUND));

        UUID currentUser = SecurityUtils.currentUser().userPublicId();

        if (!notification.getReceiverId().equals(currentUser)) {
            throw new BusinessException(ErrorCode.USER_NOT_ALLOWED);
        }

        notification.markAsRead();

        notificationRepo.save(notification);
    }
}
