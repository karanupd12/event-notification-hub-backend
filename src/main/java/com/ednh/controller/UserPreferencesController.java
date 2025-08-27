package com.ednh.controller;

import com.ednh.dto.request.UserPreferencesRequest;
import com.ednh.dto.response.ApiResponse;
import com.ednh.dto.response.UserPreferencesResponse;
import com.ednh.entity.User;
import com.ednh.entity.UserPreferences;
import com.ednh.service.UserPreferencesService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for user notification preferences
 * Handles preference management and filtering settings
 */
@Slf4j
@RestController
@RequestMapping("/preferences")
@RequiredArgsConstructor
public class UserPreferencesController {

    private final UserPreferencesService preferencesService;

    @GetMapping
    public ResponseEntity<ApiResponse<UserPreferencesResponse>> getUserPreferences(
            @AuthenticationPrincipal User user) {

        log.info("Fetching preferences for user: {}", user.getUsername());

        UserPreferences preferences = preferencesService.getUserPreferences(user.getId());
        UserPreferencesResponse response = UserPreferencesResponse.fromUserPreferences(preferences);

        return ResponseEntity.ok(ApiResponse.success(
                "User preferences retrieved successfully", response));
    }

    @PutMapping
    public ResponseEntity<ApiResponse<UserPreferencesResponse>> updateUserPreferences(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody UserPreferencesRequest request) {

        log.info("Updating preferences for user: {}", user.getUsername());

        UserPreferences preferences = preferencesService.updateUserPreferences(
                user.getId(), request);
        UserPreferencesResponse response = UserPreferencesResponse.fromUserPreferences(preferences);

        return ResponseEntity.ok(ApiResponse.success(
                "User preferences updated successfully", response));
    }

    @PostMapping("/reset")
    public ResponseEntity<ApiResponse<UserPreferencesResponse>> resetToDefaults(
            @AuthenticationPrincipal User user) {

        log.info("Resetting preferences to defaults for user: {}", user.getUsername());

        preferencesService.resetToDefaults(user.getId());
        UserPreferences preferences = preferencesService.getUserPreferences(user.getId());
        UserPreferencesResponse response = UserPreferencesResponse.fromUserPreferences(preferences);

        return ResponseEntity.ok(ApiResponse.success(
                "Preferences reset to defaults successfully", response));
    }

    @PostMapping("/apps/{appId}/mute")
    public ResponseEntity<ApiResponse<String>> muteApp(
            @AuthenticationPrincipal User user,
            @PathVariable String appId) {

        log.info("Muting app {} for user: {}", appId, user.getUsername());

        preferencesService.muteApp(user.getId(), appId);

        return ResponseEntity.ok(ApiResponse.success(
                "App muted successfully", "App " + appId + " has been muted"));
    }

    @PostMapping("/apps/{appId}/unmute")
    public ResponseEntity<ApiResponse<String>> unmuteApp(
            @AuthenticationPrincipal User user,
            @PathVariable String appId) {

        log.info("Unmuting app {} for user: {}", appId, user.getUsername());

        preferencesService.unmuteApp(user.getId(), appId);

        return ResponseEntity.ok(ApiResponse.success(
                "App unmuted successfully", "App " + appId + " has been unmuted"));
    }

    @PutMapping("/quiet-hours")
    public ResponseEntity<ApiResponse<UserPreferencesResponse>> updateQuietHours(
            @AuthenticationPrincipal User user,
            @RequestBody UserPreferencesRequest request) {

        log.info("Updating quiet hours for user: {}", user.getUsername());

        // Only update quiet hours related fields
        UserPreferencesRequest quietHoursRequest = UserPreferencesRequest.builder()
                .quietHoursEnabled(request.getQuietHoursEnabled())
                .quietHoursStart(request.getQuietHoursStart())
                .quietHoursEnd(request.getQuietHoursEnd())
                .quietHoursDays(request.getQuietHoursDays())
                .build();

        UserPreferences preferences = preferencesService.updateUserPreferences(
                user.getId(), quietHoursRequest);
        UserPreferencesResponse response = UserPreferencesResponse.fromUserPreferences(preferences);

        return ResponseEntity.ok(ApiResponse.success(
                "Quiet hours updated successfully", response));
    }

    @PutMapping("/notifications/toggle")
    public ResponseEntity<ApiResponse<String>> toggleNotifications(
            @AuthenticationPrincipal User user) {

        UserPreferences currentPrefs = preferencesService.getUserPreferences(user.getId());
        boolean newState = !currentPrefs.isNotificationsEnabled();

        UserPreferencesRequest request = UserPreferencesRequest.builder()
                .notificationsEnabled(newState)
                .build();

        preferencesService.updateUserPreferences(user.getId(), request);

        String message = newState ? "Notifications enabled" : "Notifications disabled";
        log.info("{} for user: {}", message, user.getUsername());

        return ResponseEntity.ok(ApiResponse.success(message, String.valueOf(newState)));
    }
}
