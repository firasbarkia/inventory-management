package com.inventory.service;

import com.inventory.model.Notification;

import java.util.List;
import java.util.Optional;

public interface INotificationService {
    Notification createShortageNotification(Long itemId, String message);
    List<Notification> getAllNotifications();
    Optional<Notification> getNotificationById(Long id);
    Notification approveNotification(Long notificationId, Long adminId);
    Notification disapproveNotification(Long notificationId, Long adminId);
}
