package com.yotor.global_logistics.notification.persistence;

import com.yotor.global_logistics.notification.application.dto.UnreadCountProjection;
import com.yotor.global_logistics.notification.domain.Notification;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NotificationRepository extends CrudRepository<Notification,Long> {

    @Query("""
        SELECT * FROM notification
        WHERE receiver_id = :receiverId
        ORDER BY created_at DESC
        LIMIT :limit
    """)
    List<Notification> findLatest(UUID receiverId, int limit);

    @Query("""
        SELECT COUNT(*)
        FROM notification
        WHERE receiver_id = :receiverId
        AND is_read = false
    """)
    long countUnread(UUID receiverId);

    @Modifying
    @Query("""
        UPDATE notification
        SET is_read = true, read_at = NOW()
        WHERE public_id = :publicId
    """)
    void markAsRead(UUID publicId);

    @Query("""
        SELECT reference_type, COUNT(*) AS total
        FROM notification
        WHERE receiver_id = :receiverId
          AND is_read = false
        GROUP BY reference_type
    """)
    List<UnreadCountProjection> countUnreadGroupByReference(UUID receiverId);

    @Query("""
        SELECT *
        FROM notification
        WHERE receiver_id = :receiverId
          AND reference_type = :referenceType
        ORDER BY created_at DESC
        LIMIT :limit
    """)
    List<Notification> findByReferenceType(
            UUID receiverId,
            String referenceType,
            int limit
    );

    @Query("""
            SELECT * FROM notification 
            WHERE public_id = :publicId
            """)
    Optional<Notification> findByPublicId(UUID publicId);
}
