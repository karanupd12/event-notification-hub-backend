package com.ednh.service;

import com.ednh.dto.request.WebhookNotificationRequest;
import com.ednh.entity.Application;
import com.ednh.entity.Notification;
import com.ednh.entity.User;
import com.ednh.repository.NotificationRepository;
import com.ednh.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service for managing notifications
 * Handles notification creation, storage, and real-time delivery
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    private UserPreferencesService preferencesService;

    @Transactional
    public Notification createNotification(
            WebhookNotificationRequest request,
            Application app,
            String sourceIp,
            String userAgent) {

        // Validate user exists
        Optional<User> user = userRepository.findById(request.getUserId());
        if (user.isEmpty()) {
            throw new RuntimeException("User not found: " + request.getUserId());
        }

        // Create notification entity
        Notification notification = Notification.builder()
                .appId(app.getAppId())
                .userId(request.getUserId())
                .title(request.getTitle())
                .message(request.getMessage())
                .type(request.getType())
                .priority(request.getPriority())
                .data(request.getData())
                .tenantId(request.getTenantId())
                .sourceIp(sourceIp)
                .userAgent(userAgent)
                .build();

        // Check user preferences before saving/delivering
        if (!preferencesService.shouldDeliverNotification(notification, request.getUserId())) {
            log.info("Notification filtered by user preferences for user: {}", request.getUserId());
            // Save the notification but mark it as filtered
            notification.setStatus(Notification.Status.ARCHIVED);
            return notificationRepository.save(notification);
        }

        // Save to database
        notification = notificationRepository.save(notification);

        log.info("Notification created: {} for user: {} from app: {}",
                notification.getId(), request.getUserId(), app.getAppId());

        // Send real-time notification only if preferences allow
        sendRealTimeNotification(notification);

        return notification;
    }

    private void sendRealTimeNotification(Notification notification) {
        try {
            // Send to user-specific topic
            String destination = "/topic/notifications/" + notification.getUserId();
            messagingTemplate.convertAndSend(destination, notification);

            // Mark as delivered
            notification.markAsDelivered();
            notificationRepository.save(notification);

            log.debug("Real-time notification sent to: {}", destination);
        } catch (Exception e) {
            log.error("Failed to send real-time notification: {}", e.getMessage());
        }
    }

    public Page<Notification> getUserNotifications(String userId, Pageable pageable) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }

    public Page<Notification> getUserNotificationsByStatus(
            String userId, Notification.Status status, Pageable pageable) {
        return notificationRepository.findByUserIdAndStatusOrderByCreatedAtDesc(
                userId, status, pageable);
    }

    public long getUnreadCount(String userId) {
        return notificationRepository.countByUserIdAndStatus(userId, Notification.Status.UNREAD);
    }

    @Transactional
    public Notification markAsRead(String notificationId, String userId) {
        Optional<Notification> notificationOpt = notificationRepository.findById(notificationId);

        if (notificationOpt.isEmpty()) {
            throw new RuntimeException("Notification not found");
        }

        Notification notification = notificationOpt.get();

        if (!notification.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized access to notification");
        }

        notification.markAsRead();
        return notificationRepository.save(notification);
    }

    // Add these methods to your existing NotificationService class

    public long getTotalCount(String userId) {
        return notificationRepository.countByUserId(userId);
    }

    public Notification getNotificationById(String notificationId, String userId) {
        Optional<Notification> notificationOpt = notificationRepository.findById(notificationId);

        if (notificationOpt.isEmpty()) {
            throw new RuntimeException("Notification not found");
        }

        Notification notification = notificationOpt.get();

        if (!notification.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized access to notification");
        }

        return notification;
    }

    @Transactional
    public int performBulkAction(String action, List<String> notificationIds, String userId) {
        int updatedCount = 0;

        for (String notificationId : notificationIds) {
            try {
                Notification notification = getNotificationById(notificationId, userId);

                switch (action.toLowerCase()) {
                    case "read":
                        if (notification.getStatus() != Notification.Status.READ) {
                            notification.markAsRead();
                            notificationRepository.save(notification);
                            updatedCount++;
                        }
                        break;
                    case "unread":
                        if (notification.getStatus() != Notification.Status.UNREAD) {
                            notification.setStatus(Notification.Status.UNREAD);
                            notification.setReadAt(null);
                            notificationRepository.save(notification);
                            updatedCount++;
                        }
                        break;
                    case "archive":
                        if (!notification.isArchived()) {
                            notification.archive();
                            notificationRepository.save(notification);
                            updatedCount++;
                        }
                        break;
                    case "delete":
                        notificationRepository.delete(notification);
                        updatedCount++;
                        break;
                    default:
                        throw new RuntimeException("Invalid action: " + action);
                }
            } catch (RuntimeException e) {
                log.warn("Failed to perform action '{}' on notification {}: {}",
                        action, notificationId, e.getMessage());
            }
        }

        return updatedCount;
    }

    @Transactional
    public void deleteNotification(String notificationId, String userId) {
        Notification notification = getNotificationById(notificationId, userId);
        notificationRepository.delete(notification);
        log.info("Notification {} deleted for user: {}", notificationId, userId);
    }

}
