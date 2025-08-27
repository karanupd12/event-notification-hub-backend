package com.ednh.dto.request;

import com.ednh.entity.Notification;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;

/**
 * Request DTO for updating user preferences
 * Allows partial updates of notification settings
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPreferencesRequest {

    // Global settings
    private Boolean notificationsEnabled;
    private Boolean emailNotifications;
    private Boolean pushNotifications;
    private Boolean soundEnabled;

    // App management
    private List<String> mutedApps;
    private List<String> favoriteApps;

    // Type filtering
    private List<Notification.NotificationType> enabledTypes;
    private List<Notification.NotificationType> mutedTypes;

    // Priority settings
    private Notification.Priority minimumPriority;

    // Quiet hours
    private Boolean quietHoursEnabled;
    private LocalTime quietHoursStart;
    private LocalTime quietHoursEnd;
    private List<String> quietHoursDays;

    // Keyword filtering
    private List<String> mutedKeywords;
    private List<String> priorityKeywords;

    // Delivery preferences
    private Integer maxNotificationsPerHour;
    private Boolean groupSimilarNotifications;
    private Integer groupingTimeWindowMinutes;

    // Auto-cleanup
    private Boolean autoArchiveEnabled;
    private Integer autoArchiveAfterDays;
    private Boolean autoDeleteEnabled;
    private Integer autoDeleteAfterDays;

    // Custom rules
    private Map<String, Object> customRules;
}
