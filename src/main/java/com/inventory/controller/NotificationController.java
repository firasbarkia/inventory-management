package com.inventory.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.inventory.model.Notification;
import com.inventory.model.SupplierNotification;
import com.inventory.model.User;
import com.inventory.model.WorkerNotification;
import com.inventory.repository.ItemRepository;
import com.inventory.repository.NotificationRepository;
import com.inventory.repository.RequestRepository;
import com.inventory.repository.SupplierNotificationRepository;
import com.inventory.repository.UserRepository;
import com.inventory.repository.WorkerNotificationRepository;
@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final SupplierNotificationRepository supplierNotificationRepository;
    private final WorkerNotificationRepository workerNotificationRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final RequestRepository requestRepository;
    private final NotificationRepository notificationRepository;

    public NotificationController(SupplierNotificationRepository supplierNotificationRepository,
                                WorkerNotificationRepository workerNotificationRepository,
                                UserRepository userRepository,
                                ItemRepository itemRepository,
                                RequestRepository requestRepository,
                                NotificationRepository notificationRepository) {
        this.supplierNotificationRepository = supplierNotificationRepository;
        this.workerNotificationRepository = workerNotificationRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.requestRepository = requestRepository;
        this.notificationRepository = notificationRepository;
    }

    // --- Supplier Notifications ---

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/supplier/check-low-stock/{itemId}")
    public ResponseEntity<?> checkAndNotifyLowStock(@PathVariable Long itemId) {
        // This method now creates an admin notification first
        return itemRepository.findById(itemId).map(item -> {
            if (item.getQuantity() < item.getMinStockLevel()) {
                // Create notification for admin instead of directly for supplier
                Notification notification = new Notification();
                notification.setMessage(String.format(
                    "Low stock alert - Item: %s (ID: %d) | Current: %d, Min: %d",
                    item.getName(), item.getId(),
                    item.getQuantity(), item.getMinStockLevel()));
                notification.setStatus("PENDING");
                notification.setItem(item);
                return ResponseEntity.status(HttpStatus.CREATED)
                    .body(notificationRepository.save(notification));
            }
            return ResponseEntity.ok("Item stock level is sufficient for " + item.getName());
        }).orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/supplier")
    public ResponseEntity<List<SupplierNotification>> getAllSupplierNotifications() {
        return ResponseEntity.ok(supplierNotificationRepository.findAll());
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/supplier/{id}")
    public ResponseEntity<SupplierNotification> getSupplierNotificationById(@PathVariable Long id) {
        return supplierNotificationRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/supplier/{id}/approve")
    public ResponseEntity<?> approveSupplierNotification(@PathVariable Long id) {
        return supplierNotificationRepository.findById(id)
                .map(notification -> {
                    notification.setStatus("APPROVED");
                    return ResponseEntity.ok(supplierNotificationRepository.save(notification));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/supplier/{id}/reject")
    public ResponseEntity<?> rejectSupplierNotification(@PathVariable Long id) {
        return supplierNotificationRepository.findById(id)
                .map(notification -> {
                    notification.setStatus("REJECTED");
                    return ResponseEntity.ok(supplierNotificationRepository.save(notification));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/supplier/{id}")
    public ResponseEntity<Void> deleteSupplierNotification(@PathVariable Long id) {
        supplierNotificationRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // --- Worker Notifications ---

    @PreAuthorize("hasAnyAuthority('WORKER')")
    @PostMapping("/worker/request-notification/{requestId}")
    public ResponseEntity<?> createRequestNotification(@PathVariable Long requestId) {
        return requestRepository.findById(requestId).map(request -> {
            List<User> workers = userRepository.findByRoles("WORKER");
            if (workers.isEmpty()) {
                return ResponseEntity.ok("No workers found to notify for request ID: " + requestId);
            }

            workers.forEach(worker -> {
                WorkerNotification notification = new WorkerNotification();
                notification.setMessage("New request for item: " +
                    (request.getItem() != null ? request.getItem().getName() : "Unknown"));
                notification.setStatus("UNREAD");
                notification.setUser(worker); // Link to worker
                notification.setRequest(request); // Link to request
                workerNotificationRepository.save(notification);
            });

            return ResponseEntity.status(HttpStatus.CREATED)
                .body("Notifications sent to workers for request ID: " + requestId);
        }).orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'WORKER')") // Or just WORKER if they only see their own
    @GetMapping("/worker")
    public ResponseEntity<List<WorkerNotification>> getAllWorkerNotifications() {
        // Add logic here to filter by current user if needed
        return ResponseEntity.ok(workerNotificationRepository.findAll());
    }


    @PreAuthorize("hasAnyAuthority('ADMIN', 'WORKER')")
    @DeleteMapping("/worker/{id}")
    public ResponseEntity<Void> deleteWorkerNotification(@PathVariable Long id) {
        // Add logic here to check if current user owns this notification before deleting
        workerNotificationRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
