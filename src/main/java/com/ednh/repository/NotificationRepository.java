package com.ednh.repository;

import com.ednh.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for Notification entity
 * Provides queries for notification management and filtering
 */
@Repository
public interface NotificationRepository extends MongoRepository<Notification, String> {

    // Find notifications by user
    Page<Notification> findByUserIdOrderByCreatedAtDesc(String userId, Pageable pageable);

    Page<Notification> findByUserIdAndStatusOrderByCreatedAtDesc(
            String userId, Notification.Status status, Pageable pageable);

    // Find by application
    Page<Notification> findByAppIdOrderByCreatedAtDesc(String appId, Pageable pageable);

    // Add this method to your existing NotificationRepository interface
    long countByUserId(String userId);

    // Count unread notifications
    long countByUserIdAndStatus(String userId, Notification.Status status);

    // Find notifications by type and priority
    @Query("{'userId': ?0, 'type': ?1, 'priority': ?2}")
    List<Notification> findByUserIdAndTypeAndPriority(
            String userId, Notification.NotificationType type, Notification.Priority priority);

    // Find recent notifications
    List<Notification> findByUserIdAndCreatedAtAfterOrderByCreatedAtDesc(
            String userId, LocalDateTime since);

    // Multi-tenant queries
    Page<Notification> findByUserIdAndTenantIdOrderByCreatedAtDesc(
            String userId, String tenantId, Pageable pageable);

    // Cleanup queries
    List<Notification> findByArchivedTrueAndArchivedAtBefore(LocalDateTime before);

    void deleteByStatusAndCreatedAtBefore(Notification.Status status, LocalDateTime before);
}
