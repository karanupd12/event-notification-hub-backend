package com.ednh.websocket;

import com.ednh.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * WebSocket message controller for real-time notifications
 * Handles WebSocket subscriptions and message routing
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class NotificationWebSocketController {

    @SubscribeMapping("/notifications/{userId}")
    public Map<String, Object> subscribeToNotifications(
            Principal principal,
            @AuthenticationPrincipal User user) {

        if (user != null) {
            log.info("User {} subscribed to notification feed", user.getUsername());

            return Map.of(
                    "type", "subscription_confirmed",
                    "message", "Successfully subscribed to notifications",
                    "userId", user.getId(),
                    "timestamp", LocalDateTime.now().toString()
            );
        }

        return Map.of(
                "type", "error",
                "message", "Authentication required",
                "timestamp", LocalDateTime.now().toString()
        );
    }

    @MessageMapping("/notifications/ping")
    @SendTo("/topic/notifications/ping")
    public Map<String, Object> handlePing(Map<String, Object> message) {
        return Map.of(
                "type", "pong",
                "timestamp", LocalDateTime.now().toString(),
                "original", message
        );
    }
}
