package com.ednh.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Application entity for managing webhook clients
 * Stores application credentials and configuration
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "applications")
public class Application {

    @Id
    private String id;

    @Indexed(unique = true)
    private String appId;

    private String name;
    private String description;

    // JWT secret key for this application
    private String secretKey;

    // Allowed notification types
    private List<Notification.NotificationType> allowedTypes;

    // Rate limiting
    @Builder.Default
    private int rateLimit = 1000; // requests per hour

    @Builder.Default
    private boolean enabled = true;

    // Owner/creator of this application
    private String ownerId;

    // Multi-tenant support
    private String tenantId;

    // Audit fields
    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    // Usage statistics
    @Builder.Default
    private long totalNotificationsSent = 0;

    private LocalDateTime lastUsedAt;
}
