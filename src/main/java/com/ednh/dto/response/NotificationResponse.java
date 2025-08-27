package com.ednh.dto.response;

import com.ednh.entity.Notification;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Response DTO for notification data
 * Provides formatted notification information for API responses
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {

    private String id;
    private String appId;
    private String title;
    private String message;
    private Notification.NotificationType type;
    private Notification.Priority priority;
    private Notification.Status status;
    private Map<String, Object> data;
    private boolean delivered;
    private LocalDateTime createdAt;
    private LocalDateTime readAt;
    private LocalDateTime deliveredAt;

    public static NotificationResponse fromNotification(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .appId(notification.getAppId())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .type(notification.getType())
                .priority(notification.getPriority())
                .status(notification.getStatus())
                .data(notification.getData())
                .delivered(notification.isDelivered())
                .createdAt(notification.getCreatedAt())
                .readAt(notification.getReadAt())
                .deliveredAt(notification.getDeliveredAt())
                .build();
    }
}
