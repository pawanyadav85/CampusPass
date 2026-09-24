package com.campuspass.service;

import com.campuspass.dto.response.NotificationResponse;

import java.util.List;

public interface NotificationService {
    void createNotification(Long userId, String title, String message, String notificationType, Long referenceId);
    List<NotificationResponse> getUserNotifications(Long userId);
    long getUnreadCount(Long userId);
    void markAsRead(Long notificationId, Long userId);
    void markAllAsRead(Long userId);
}
