package com.ednh.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Notification entity representing incoming webhook notifications
 * Stores notification data with metadata and delivery tracking
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "notifications")
public class Notification {

    @Id
    private String id;

    // Application that sent the notification
    @Indexed
    private String appId;

    // User who should receive the notification
    @Indexed
    private String userId;

    // Notification metadata
    private String title;
    private String message;
    private NotificationType type;
    private Priority priority;

    // Additional data payload
    private Map<String, Object> data;

    // Delivery tracking
    @Builder.Default
    private Status status = Status.UNREAD;

    @Builder.Default
    private boolean delivered = false;

    private LocalDateTime deliveredAt;

    private LocalDateTime readAt;

    // Audit fields
    @CreatedDate
    private LocalDateTime createdAt;

    @Builder.Default
    private boolean archived = false;

    private LocalDateTime archivedAt;

    // Multi-tenant support
    private String tenantId;

    // Source tracking
    private String sourceIp;
    private String userAgent;

    public enum NotificationType {
        INFO, SUCCESS, WARNING, ERROR, ALERT, SYSTEM
    }

    public enum Priority {
        LOW, NORMAL, HIGH, URGENT
    }

    public enum Status {
        UNREAD, READ, ARCHIVED, DELETED
    }

    public void markAsRead() {
        this.status = Status.READ;
        this.readAt = LocalDateTime.now();
    }

    public void markAsDelivered() {
        this.delivered = true;
        this.deliveredAt = LocalDateTime.now();
    }

    public void archive() {
        this.status = Status.ARCHIVED;
        this.archived = true;
        this.archivedAt = LocalDateTime.now();
    }
}
