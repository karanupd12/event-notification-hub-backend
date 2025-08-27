package com.ednh.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for webhook operations
 * Returns notification delivery status and metadata
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebhookResponse {

    private String notificationId;
    private String status;
    private String message;
    private boolean delivered;
    private String timestamp;

    public static WebhookResponse success(String notificationId, boolean delivered) {
        return WebhookResponse.builder()
                .notificationId(notificationId)
                .status("success")
                .message("Notification processed successfully")
                .delivered(delivered)
                .timestamp(java.time.LocalDateTime.now().toString())
                .build();
    }

    public static WebhookResponse error(String message) {
        return WebhookResponse.builder()
                .status("error")
                .message(message)
                .delivered(false)
                .timestamp(java.time.LocalDateTime.now().toString())
                .build();
    }
}
