package com.ednh.controller;

import com.ednh.dto.request.WebhookNotificationRequest;
import com.ednh.dto.response.ApiResponse;
import com.ednh.dto.response.WebhookResponse;
import com.ednh.entity.Application;
import com.ednh.entity.Notification;
import com.ednh.service.ApplicationService;
import com.ednh.service.NotificationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * Webhook controller for receiving external notifications
 * Handles JWT validation and notification ingestion
 */
@Slf4j
@RestController
@RequestMapping("/webhook")
@RequiredArgsConstructor
public class WebhookController {

    private final NotificationService notificationService;
    private final ApplicationService applicationService;

    @PostMapping("/{appId}")
    public ResponseEntity<ApiResponse<WebhookResponse>> receiveNotification(
            @PathVariable String appId,
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody WebhookNotificationRequest request,
            HttpServletRequest httpRequest) {

        log.info("Webhook notification received from app: {} for user: {}", appId, request.getUserId());

        try {
            // Validate Authorization header format
            if (!authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("Invalid Authorization header format"));
            }

            String token = authHeader.substring(7);

            // Validate application and token
            Optional<Application> appOpt = applicationService.findByAppId(appId);
            if (appOpt.isEmpty()) {
                log.warn("Unknown application ID: {}", appId);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("Invalid application ID"));
            }

            Application app = appOpt.get();

            // Validate JWT token
            if (!applicationService.validateApplicationToken(appId, token)) {
                log.warn("Invalid token for application: {}", appId);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("Invalid or expired token"));
            }

            // Extract client information
            String sourceIp = getClientIpAddress(httpRequest);
            String userAgent = httpRequest.getHeader("User-Agent");

            // Create notification
            Notification notification = notificationService.createNotification(
                    request, app, sourceIp, userAgent);

            // Update application statistics
            applicationService.incrementNotificationCount(appId);

            // Return success response
            WebhookResponse webhookResponse = WebhookResponse.success(
                    notification.getId(), notification.isDelivered());

            ApiResponse<WebhookResponse> response = ApiResponse.success(
                    "Notification processed successfully", webhookResponse);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            log.error("Error processing webhook from app {}: {}", appId, e.getMessage());

            WebhookResponse errorResponse = WebhookResponse.error(e.getMessage());
            ApiResponse<WebhookResponse> response = ApiResponse.error(e.getMessage());
            response.setData(errorResponse);

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);

        } catch (Exception e) {
            log.error("Unexpected error processing webhook from app {}: {}", appId, e.getMessage());

            WebhookResponse errorResponse = WebhookResponse.error("Internal server error");
            ApiResponse<WebhookResponse> response = ApiResponse.error("Internal server error");
            response.setData(errorResponse);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/{appId}/status")
    public ResponseEntity<ApiResponse<String>> getWebhookStatus(@PathVariable String appId) {
        Optional<Application> appOpt = applicationService.findByAppId(appId);

        if (appOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Application not found"));
        }

        Application app = appOpt.get();
        String status = app.isEnabled() ? "active" : "disabled";

        return ResponseEntity.ok(ApiResponse.success(
                "Webhook status retrieved",
                status + " - Total notifications sent: " + app.getTotalNotificationsSent()));
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }

        return request.getRemoteAddr();
    }
}
