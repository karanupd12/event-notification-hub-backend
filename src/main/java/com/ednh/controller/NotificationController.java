package com.ednh.controller;

import com.ednh.dto.request.NotificationActionRequest;
import com.ednh.dto.response.ApiResponse;
import com.ednh.dto.response.NotificationFeedResponse;
import com.ednh.dto.response.NotificationResponse;
import com.ednh.entity.Notification;
import com.ednh.entity.User;
import com.ednh.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller for notification feed and management
 * Provides user-facing APIs for notification retrieval and actions
 */
@Slf4j
@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<ApiResponse<NotificationFeedResponse>> getNotifications(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String priority) {

        log.info("Fetching notifications for user: {} (page: {}, size: {})",
                user.getUsername(), page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<Notification> notifications;

        // Filter by status if provided
        if (status != null && !status.trim().isEmpty()) {
            try {
                Notification.Status statusEnum = Notification.Status.valueOf(status.toUpperCase());
                notifications = notificationService.getUserNotificationsByStatus(
                        user.getId(), statusEnum, pageable);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Invalid status value: " + status));
            }
        } else {
            notifications = notificationService.getUserNotifications(user.getId(), pageable);
        }

        // Get unread count
        long unreadCount = notificationService.getUnreadCount(user.getId());

        NotificationFeedResponse feedResponse = NotificationFeedResponse.fromPage(notifications, unreadCount);

        return ResponseEntity.ok(ApiResponse.success(
                "Notifications retrieved successfully", feedResponse));
    }

    @GetMapping("/unread")
    public ResponseEntity<ApiResponse<NotificationFeedResponse>> getUnreadNotifications(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        log.info("Fetching unread notifications for user: {}", user.getUsername());

        Pageable pageable = PageRequest.of(page, size);
        Page<Notification> notifications = notificationService.getUserNotificationsByStatus(
                user.getId(), Notification.Status.UNREAD, pageable);

        long unreadCount = notificationService.getUnreadCount(user.getId());
        NotificationFeedResponse feedResponse = NotificationFeedResponse.fromPage(notifications, unreadCount);

        return ResponseEntity.ok(ApiResponse.success(
                "Unread notifications retrieved successfully", feedResponse));
    }

    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getNotificationCounts(
            @AuthenticationPrincipal User user) {

        long unreadCount = notificationService.getUnreadCount(user.getId());
        long totalCount = notificationService.getTotalCount(user.getId());

        Map<String, Long> counts = Map.of(
                "unread", unreadCount,
                "total", totalCount
        );

        return ResponseEntity.ok(ApiResponse.success(
                "Notification counts retrieved successfully", counts));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<ApiResponse<NotificationResponse>> markAsRead(
            @PathVariable String id,
            @AuthenticationPrincipal User user) {

        log.info("Marking notification {} as read for user: {}", id, user.getUsername());

        try {
            Notification notification = notificationService.markAsRead(id, user.getId());
            NotificationResponse response = NotificationResponse.fromNotification(notification);

            return ResponseEntity.ok(ApiResponse.success(
                    "Notification marked as read", response));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/bulk-action")
    public ResponseEntity<ApiResponse<String>> bulkAction(
            @Valid @RequestBody NotificationActionRequest request,
            @AuthenticationPrincipal User user) {

        log.info("Performing bulk action '{}' on {} notifications for user: {}",
                request.getAction(), request.getNotificationIds().size(), user.getUsername());

        try {
            int updatedCount = notificationService.performBulkAction(
                    request.getAction(), request.getNotificationIds(), user.getId());

            return ResponseEntity.ok(ApiResponse.success(
                    String.format("Successfully performed '%s' action on %d notifications",
                            request.getAction(), updatedCount),
                    String.valueOf(updatedCount)));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteNotification(
            @PathVariable String id,
            @AuthenticationPrincipal User user) {

        log.info("Deleting notification {} for user: {}", id, user.getUsername());

        try {
            notificationService.deleteNotification(id, user.getId());
            return ResponseEntity.ok(ApiResponse.success(
                    "Notification deleted successfully", null));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<NotificationResponse>> getNotification(
            @PathVariable String id,
            @AuthenticationPrincipal User user) {

        try {
            Notification notification = notificationService.getNotificationById(id, user.getId());
            NotificationResponse response = NotificationResponse.fromNotification(notification);

            return ResponseEntity.ok(ApiResponse.success(
                    "Notification retrieved successfully", response));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}
