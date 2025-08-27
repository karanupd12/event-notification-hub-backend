package com.ednh.service;

import com.ednh.dto.request.UserPreferencesRequest;
import com.ednh.entity.Notification;
import com.ednh.entity.UserPreferences;
import com.ednh.repository.UserPreferencesRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Service for managing user notification preferences
 * Handles preference creation, updates, and notification filtering
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserPreferencesService {

    private final UserPreferencesRepository preferencesRepository;

    public UserPreferences getUserPreferences(String userId) {
        return preferencesRepository.findByUserId(userId)
                .orElseGet(() -> createDefaultPreferences(userId));
    }

    @Transactional
    public UserPreferences createDefaultPreferences(String userId) {
        UserPreferences defaultPreferences = UserPreferences.builder()
                .userId(userId)
                .notificationsEnabled(true)
                .emailNotifications(true)
                .pushNotifications(true)
                .soundEnabled(true)
                .enabledTypes(Arrays.asList(Notification.NotificationType.values()))
                .minimumPriority(Notification.Priority.LOW)
                .quietHoursEnabled(false)
                .maxNotificationsPerHour(50)
                .groupSimilarNotifications(true)
                .groupingTimeWindowMinutes(30)
                .autoArchiveEnabled(true)
                .autoArchiveAfterDays(30)
                .autoDeleteEnabled(false)
                .autoDeleteAfterDays(90)
                .build();

        UserPreferences saved = preferencesRepository.save(defaultPreferences);
        log.info("Created default preferences for user: {}", userId);
        return saved;
    }

    @Transactional
    public UserPreferences updateUserPreferences(String userId, UserPreferencesRequest request) {
        UserPreferences preferences = getUserPreferences(userId);

        // Update fields if they are provided in the request (partial updates)
        if (request.getNotificationsEnabled() != null) {
            preferences.setNotificationsEnabled(request.getNotificationsEnabled());
        }
        if (request.getEmailNotifications() != null) {
            preferences.setEmailNotifications(request.getEmailNotifications());
        }
        if (request.getPushNotifications() != null) {
            preferences.setPushNotifications(request.getPushNotifications());
        }
        if (request.getSoundEnabled() != null) {
            preferences.setSoundEnabled(request.getSoundEnabled());
        }
        if (request.getMutedApps() != null) {
            preferences.setMutedApps(request.getMutedApps());
        }
        if (request.getFavoriteApps() != null) {
            preferences.setFavoriteApps(request.getFavoriteApps());
        }
        if (request.getEnabledTypes() != null) {
            preferences.setEnabledTypes(request.getEnabledTypes());
        }
        if (request.getMutedTypes() != null) {
            preferences.setMutedTypes(request.getMutedTypes());
        }
        if (request.getMinimumPriority() != null) {
            preferences.setMinimumPriority(request.getMinimumPriority());
        }
        if (request.getQuietHoursEnabled() != null) {
            preferences.setQuietHoursEnabled(request.getQuietHoursEnabled());
        }
        if (request.getQuietHoursStart() != null) {
            preferences.setQuietHoursStart(request.getQuietHoursStart());
        }
        if (request.getQuietHoursEnd() != null) {
            preferences.setQuietHoursEnd(request.getQuietHoursEnd());
        }
        if (request.getQuietHoursDays() != null) {
            preferences.setQuietHoursDays(request.getQuietHoursDays());
        }
        if (request.getMutedKeywords() != null) {
            preferences.setMutedKeywords(request.getMutedKeywords());
        }
        if (request.getPriorityKeywords() != null) {
            preferences.setPriorityKeywords(request.getPriorityKeywords());
        }
        if (request.getMaxNotificationsPerHour() != null) {
            preferences.setMaxNotificationsPerHour(request.getMaxNotificationsPerHour());
        }
        if (request.getGroupSimilarNotifications() != null) {
            preferences.setGroupSimilarNotifications(request.getGroupSimilarNotifications());
        }
        if (request.getGroupingTimeWindowMinutes() != null) {
            preferences.setGroupingTimeWindowMinutes(request.getGroupingTimeWindowMinutes());
        }
        if (request.getAutoArchiveEnabled() != null) {
            preferences.setAutoArchiveEnabled(request.getAutoArchiveEnabled());
        }
        if (request.getAutoArchiveAfterDays() != null) {
            preferences.setAutoArchiveAfterDays(request.getAutoArchiveAfterDays());
        }
        if (request.getAutoDeleteEnabled() != null) {
            preferences.setAutoDeleteEnabled(request.getAutoDeleteEnabled());
        }
        if (request.getAutoDeleteAfterDays() != null) {
            preferences.setAutoDeleteAfterDays(request.getAutoDeleteAfterDays());
        }
        if (request.getCustomRules() != null) {
            preferences.setCustomRules(request.getCustomRules());
        }

        UserPreferences saved = preferencesRepository.save(preferences);
        log.info("Updated preferences for user: {}", userId);
        return saved;
    }

    public boolean shouldDeliverNotification(Notification notification, String userId) {
        UserPreferences preferences = getUserPreferences(userId);

        // Check if notifications are globally enabled
        if (!preferences.isNotificationsEnabled()) {
            log.debug("Notifications disabled for user: {}", userId);
            return false;
        }

        // Check if app is muted
        if (preferences.isAppMuted(notification.getAppId())) {
            log.debug("App {} is muted for user: {}", notification.getAppId(), userId);
            return false;
        }

        // Check if notification type is muted
        if (preferences.isTypeMuted(notification.getType())) {
            log.debug("Type {} is muted for user: {}", notification.getType(), userId);
            return false;
        }

        // Check priority requirements
        if (!preferences.isPriorityMet(notification.getPriority())) {
            log.debug("Priority {} doesn't meet minimum {} for user: {}",
                    notification.getPriority(), preferences.getMinimumPriority(), userId);
            return false;
        }

        // Check quiet hours
        if (preferences.isInQuietHours(LocalTime.now())) {
            // Only allow urgent notifications during quiet hours
            if (notification.getPriority() != Notification.Priority.URGENT) {
                log.debug("Notification blocked due to quiet hours for user: {}", userId);
                return false;
            }
        }

        // Check muted keywords
        if (containsMutedKeywords(notification, preferences.getMutedKeywords())) {
            log.debug("Notification contains muted keywords for user: {}", userId);
            return false;
        }

        return true;
    }

    private boolean containsMutedKeywords(Notification notification, List<String> mutedKeywords) {
        if (mutedKeywords == null || mutedKeywords.isEmpty()) {
            return false;
        }

        String content = (notification.getTitle() + " " + notification.getMessage()).toLowerCase();
        return mutedKeywords.stream()
                .anyMatch(keyword -> content.contains(keyword.toLowerCase()));
    }

    @Transactional
    public void resetToDefaults(String userId) {
        preferencesRepository.deleteByUserId(userId);
        createDefaultPreferences(userId);
        log.info("Reset preferences to defaults for user: {}", userId);
    }

    @Transactional
    public UserPreferences muteApp(String userId, String appId) {
        UserPreferences preferences = getUserPreferences(userId);
        List<String> mutedApps = preferences.getMutedApps();

        if (mutedApps == null) {
            mutedApps = List.of(appId);
        } else if (!mutedApps.contains(appId)) {
            mutedApps = List.copyOf(mutedApps);
            mutedApps.add(appId);
        }

        preferences.setMutedApps(mutedApps);
        return preferencesRepository.save(preferences);
    }

    @Transactional
    public UserPreferences unmuteApp(String userId, String appId) {
        UserPreferences preferences = getUserPreferences(userId);
        List<String> mutedApps = preferences.getMutedApps();

        if (mutedApps != null && mutedApps.contains(appId)) {
            mutedApps = mutedApps.stream()
                    .filter(id -> !id.equals(appId))
                    .toList();
            preferences.setMutedApps(mutedApps);
            return preferencesRepository.save(preferences);
        }

        return preferences;
    }
}
