package com.ednh.dto.response;

import com.ednh.entity.Notification;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Response DTO for paginated notification feed
 * Includes pagination metadata and notification list
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationFeedResponse {

    private List<NotificationResponse> notifications;
    private long totalElements;
    private int totalPages;
    private int currentPage;
    private int pageSize;
    private boolean hasNext;
    private boolean hasPrevious;
    private long unreadCount;

    public static NotificationFeedResponse fromPage(Page<Notification> page, long unreadCount) {
        List<NotificationResponse> notifications = page.getContent()
                .stream()
                .map(NotificationResponse::fromNotification)
                .toList();

        return NotificationFeedResponse.builder()
                .notifications(notifications)
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .currentPage(page.getNumber())
                .pageSize(page.getSize())
                .hasNext(page.hasNext())
                .hasPrevious(page.hasPrevious())
                .unreadCount(unreadCount)
                .build();
    }
}
