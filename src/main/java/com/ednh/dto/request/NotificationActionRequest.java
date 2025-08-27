package com.ednh.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Request DTO for batch notification actions
 * Supports bulk operations like mark as read, delete, archive
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationActionRequest {

    @NotBlank(message = "Action is required")
    private String action; // "read", "unread", "archive", "delete"

    private List<String> notificationIds;
}
