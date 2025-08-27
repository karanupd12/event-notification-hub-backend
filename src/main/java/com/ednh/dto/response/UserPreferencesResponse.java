package com.ednh.dto.response;

import com.ednh.entity.Notification;
import com.ednh.entity.UserPreferences;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

/**
 * Response DTO for user preferences
 * Returns formatted preference data for API responses
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPreferencesResponse {

    private String id;
    private String userId;

    // Global settings
    private boolean notificationsEnabled;
    private boolean emailNotifications;
    private boolean pushNotifications;
    private boolean soundEnabled;

    // App management
    private List<String> mutedApps;
    private List<String> favoriteApps;

    // Type filtering
    private List<Notification.NotificationType> enabledTypes;
    private List<Notification.NotificationType> mutedTypes;

    // Priority settings
    private Notification.Priority minimumPriority;

    // Quiet hours
    private boolean quietHoursEnabled;
    private LocalTime quietHoursStart;
    private LocalTime quietHoursEnd;
    private List<String> quietHoursDays;

    // Keyword filtering
    private List<String> mutedKeywords;
    private List<String> priorityKeywords;

    // Delivery preferences
    private int maxNotificationsPerHour;
    private boolean groupSimilarNotifications;
    private int groupingTimeWindowMinutes;

    // Auto-cleanup
    private boolean autoArchiveEnabled;
    private int autoArchiveAfterDays;
    private boolean autoDeleteEnabled;
    private int autoDeleteAfterDays;

    // Custom rules
    private Map<String, Object> customRules;

    // Metadata
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static UserPreferencesResponse fromUserPreferences(UserPreferences preferences) {
        return UserPreferencesResponse.builder()
                .id(preferences.getId())
                .userId(preferences.getUserId())
                .notificationsEnabled(preferences.isNotificationsEnabled())
                .emailNotifications(preferences.isEmailNotifications())
                .pushNotifications(preferences.isPushNotifications())
                .soundEnabled(preferences.isSoundEnabled())
                .mutedApps(preferences.getMutedApps())
                .favoriteApps(preferences.getFavoriteApps())
                .enabledTypes(preferences.getEnabledTypes())
                .mutedTypes(preferences.getMutedTypes())
                .minimumPriority(preferences.getMinimumPriority())
                .quietHoursEnabled(preferences.isQuietHoursEnabled())
                .quietHoursStart(preferences.getQuietHoursStart())
                .quietHoursEnd(preferences.getQuietHoursEnd())
                .quietHoursDays(preferences.getQuietHoursDays())
                .mutedKeywords(preferences.getMutedKeywords())
                .priorityKeywords(preferences.getPriorityKeywords())
                .maxNotificationsPerHour(preferences.getMaxNotificationsPerHour())
                .groupSimilarNotifications(preferences.isGroupSimilarNotifications())
                .groupingTimeWindowMinutes(preferences.getGroupingTimeWindowMinutes())
                .autoArchiveEnabled(preferences.isAutoArchiveEnabled())
                .autoArchiveAfterDays(preferences.getAutoArchiveAfterDays())
                .autoDeleteEnabled(preferences.isAutoDeleteEnabled())
                .autoDeleteAfterDays(preferences.getAutoDeleteAfterDays())
                .customRules(preferences.getCustomRules())
                .createdAt(preferences.getCreatedAt())
                .updatedAt(preferences.getUpdatedAt())
                .build();
    }
}
