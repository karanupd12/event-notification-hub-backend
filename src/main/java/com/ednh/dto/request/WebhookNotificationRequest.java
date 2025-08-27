package com.ednh.dto.request;

import com.ednh.entity.Notification;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Request DTO for webhook notification ingestion
 * Represents the payload sent by external applications
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebhookNotificationRequest {

    @NotBlank(message = "User ID is required")
    private String userId;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Message is required")
    private String message;

    @NotNull(message = "Notification type is required")
    private Notification.NotificationType type;

    @Builder.Default
    private Notification.Priority priority = Notification.Priority.NORMAL;

    // Optional additional data
    private Map<String, Object> data;

    // Optional scheduling (for future enhancement)
    private String scheduleAt;

    // Optional targeting
    private String tenantId;
}
