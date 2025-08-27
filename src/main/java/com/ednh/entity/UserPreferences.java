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
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

/**
 * User preferences entity for notification customization
 * Stores user-specific notification settings and filters
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "user_preferences")
public class UserPreferences {

    @Id
    private String id;

    @Indexed(unique = true)
    private String userId;

    // Global notification settings
    @Builder.Default
    private boolean notificationsEnabled = true;

    @Builder.Default
    private boolean emailNotifications = true;

    @Builder.Default
    private boolean pushNotifications = true;

    @Builder.Default
    private boolean soundEnabled = true;

    // App-specific settings
    private List<String> mutedApps; // List of appIds to mute

    private List<String> favoriteApps; // Priority apps

    // Type-based filtering
    private List<Notification.NotificationType> enabledTypes;

    private List<Notification.NotificationType> mutedTypes;

    // Priority filtering
    private Notification.Priority minimumPriority;

    // Quiet hours
    @Builder.Default
    private boolean quietHoursEnabled = false;

    private LocalTime quietHoursStart; // e.g., 22:00

    private LocalTime quietHoursEnd; // e.g., 08:00

    private List<String> quietHoursDays; // ["MONDAY", "TUESDAY", ...]

    // Keyword filtering
    private List<String> mutedKeywords; // Notifications containing these words will be muted

    private List<String> priorityKeywords; // Notifications with these words get higher priority

    // Delivery preferences
    @Builder.Default
    private int maxNotificationsPerHour = 50;

    @Builder.Default
    private boolean groupSimilarNotifications = true;

    @Builder.Default
    private int groupingTimeWindowMinutes = 30;

    // Auto-cleanup settings
    @Builder.Default
    private boolean autoArchiveEnabled = true;

    @Builder.Default
    private int autoArchiveAfterDays = 30;

    @Builder.Default
    private boolean autoDeleteEnabled = false;

    @Builder.Default
    private int autoDeleteAfterDays = 90;

    // Custom rules (JSON format for flexibility)
    private Map<String, Object> customRules;

    // Multi-tenant support
    private String tenantId;

    // Audit fields
    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    // Helper methods
    public boolean isAppMuted(String appId) {
        return mutedApps != null && mutedApps.contains(appId);
    }

    public boolean isTypeMuted(Notification.NotificationType type) {
        return mutedTypes != null && mutedTypes.contains(type);
    }

    public boolean isInQuietHours(LocalTime currentTime) {
        if (!quietHoursEnabled || quietHoursStart == null || quietHoursEnd == null) {
            return false;
        }

        if (quietHoursStart.isBefore(quietHoursEnd)) {
            // Same day quiet hours (e.g., 22:00 - 23:59)
            return currentTime.isAfter(quietHoursStart) && currentTime.isBefore(quietHoursEnd);
        } else {
            // Overnight quiet hours (e.g., 22:00 - 08:00)
            return currentTime.isAfter(quietHoursStart) || currentTime.isBefore(quietHoursEnd);
        }
    }

    public boolean isPriorityMet(Notification.Priority priority) {
        return minimumPriority == null ||
                priority.ordinal() >= minimumPriority.ordinal();
    }
}
