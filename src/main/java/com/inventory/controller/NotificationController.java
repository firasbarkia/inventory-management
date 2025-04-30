package com.inventory.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.inventory.model.Notification;
import com.inventory.model.User;
import com.inventory.repository.ItemRepository;
import com.inventory.repository.NotificationRepository;
import com.inventory.repository.RequestRepository;
import com.inventory.repository.UserRepository;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final RequestRepository requestRepository;

    @Autowired
    public NotificationController(NotificationRepository notificationRepository,
                                UserRepository userRepository,
                                ItemRepository itemRepository,
                                RequestRepository requestRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.requestRepository = requestRepository;
    }

    @GetMapping
    public ResponseEntity<List<Notification>> getAllNotifications() {
        return ResponseEntity.ok(notificationRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Notification> getNotificationById(@PathVariable Long id) {
        return notificationRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    public ResponseEntity<?> createNotification(@RequestBody Notification notification) {
        if (notification.getAdmin() != null && notification.getAdmin().getId() != null) {
            userRepository.findById(notification.getAdmin().getId()).ifPresent(notification::setAdmin);
        }
        if (notification.getItem() != null && notification.getItem().getId() != null) {
            itemRepository.findById(notification.getItem().getId()).ifPresent(notification::setItem);
        }
        Notification saved = notificationRepository.save(notification);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateNotification(@PathVariable Long id, @RequestBody Notification notification) {
        return notificationRepository.findById(id).map(existing -> {
            existing.setMessage(notification.getMessage());
            existing.setStatus(notification.getStatus());
            Notification updated = notificationRepository.save(existing);
            return ResponseEntity.ok(updated);
        }).orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id) {
        notificationRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/check-low-stock/{itemId}")
    public ResponseEntity<?> checkAndNotifyLowStock(@PathVariable Long itemId) {
        return itemRepository.findById(itemId).map(item -> {
            if (item.getQuantity() < item.getMinStockLevel()) {
                Notification notification = new Notification();
                if (item.getSupplier() != null) {
                    notification.setMessage(String.format(
                        "Low stock alert - Item: %s (ID: %d) | Current: %d, Min: %d | Supplier: %s (ID: %d)",
                        item.getName(), item.getId(),
                        item.getQuantity(), item.getMinStockLevel(),
                        item.getSupplier().getName(), item.getSupplier().getId()));
                } else {
                    notification.setMessage(String.format(
                        "Low stock alert - Item: %s (ID: %d) | Current: %d, Min: %d | No supplier assigned",
                        item.getName(), item.getId(),
                        item.getQuantity(), item.getMinStockLevel()));
                }
                notification.setStatus("UNREAD");
                notification.setItem(item);
                return ResponseEntity.status(HttpStatus.CREATED)
                    .body(notificationRepository.save(notification));
            }
            return ResponseEntity.ok("Item stock level is sufficient for " + item.getName());
        }).orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'WORKER')")
    @PostMapping("/request-notification/{requestId}")
    public ResponseEntity<?> createRequestNotification(@PathVariable Long requestId) {
        return requestRepository.findById(requestId).map(request -> {
            List<User> workers = userRepository.findByRoles("WORKER");
            if (workers.isEmpty()) {
                return ResponseEntity.ok("No workers found to notify for request ID: " + requestId);
            }

            workers.forEach(worker -> {
                Notification notification = new Notification();
                notification.setMessage("New request for item: " + 
                    (request.getItem() != null ? request.getItem().getName() : "Unknown"));
                notification.setStatus("UNREAD");
                notification.setUser(worker);
                notification.setRequest(request);
                notificationRepository.save(notification);
            });

            return ResponseEntity.status(HttpStatus.CREATED)
                .body("Notifications sent to workers for request ID: " + requestId);
        }).orElse(ResponseEntity.notFound().build());
    }
}
