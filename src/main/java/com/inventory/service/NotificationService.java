package com.inventory.service;

import com.inventory.model.Notification;
import com.inventory.model.Item;
import com.inventory.model.User;
import com.inventory.repository.NotificationRepository;
import com.inventory.repository.ItemRepository;
import com.inventory.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NotificationService implements INotificationService {

    private final NotificationRepository notificationRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public NotificationService(NotificationRepository notificationRepository, ItemRepository itemRepository, UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    public Notification createShortageNotification(Long itemId, String message) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        Notification notification = new Notification();
        notification.setItem(item);
        notification.setMessage(message);
        notification.setStatus("PENDING");
        return notificationRepository.save(notification);
    }

    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    public Optional<Notification> getNotificationById(Long id) {
        return notificationRepository.findById(id);
    }

    public Notification approveNotification(Long notificationId, Long adminId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        notification.setAdmin(admin);
        notification.setStatus("APPROVED");
        return notificationRepository.save(notification);
    }

    public Notification disapproveNotification(Long notificationId, Long adminId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        notification.setAdmin(admin);
        notification.setStatus("DISAPPROVED");
        return notificationRepository.save(notification);
    }
}
