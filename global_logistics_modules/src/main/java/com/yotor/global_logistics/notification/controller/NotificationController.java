package com.yotor.global_logistics.notification.controller;

import com.yotor.global_logistics.notification.application.NotificationService;
import com.yotor.global_logistics.notification.application.dto.NotificationResponse;
import com.yotor.global_logistics.notification.application.dto.UnreadCountResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/notifications")
@Tag(name = "notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService service;

    @GetMapping("/latest")
    public ResponseEntity<List<NotificationResponse>> latest() {
        var res = service.getLatestForCurrentUser();
        return ResponseEntity.ok(res);
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Long> unreadCount() {
        long res = service.unreadCount();
        return ResponseEntity.ok(res);
    }

    @GetMapping("/unread-by-type")
    public ResponseEntity<List<UnreadCountResponse>> unreadByType() {
        var res = service.unreadByReferenceType();
        return ResponseEntity.ok(res);
    }

    @PostMapping("/{id}/read")
    public void markAsRead(@PathVariable UUID id) {
        service.markAsRead(id);
    }

}
