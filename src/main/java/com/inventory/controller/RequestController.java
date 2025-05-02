package com.inventory.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.inventory.model.Request;
import com.inventory.repository.ItemRepository;
import com.inventory.repository.RequestRepository;
import com.inventory.repository.UserRepository;

@RestController
@RequestMapping("/api/requests")
public class RequestController {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final com.inventory.repository.NotificationRepository notificationRepository;

    public RequestController(RequestRepository requestRepository, UserRepository userRepository, ItemRepository itemRepository, com.inventory.repository.NotificationRepository notificationRepository) {
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.notificationRepository = notificationRepository;
    }

    @GetMapping
    public ResponseEntity<List<Request>> getAllRequests() {
        return ResponseEntity.ok(requestRepository.findAll());
    }
    @PreAuthorize("hasAnyAuthority('TEACHER', 'WORKER')")
    @GetMapping("/{id}")
    public ResponseEntity<Request> getRequestById(@PathVariable Long id) {
        return requestRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAnyAuthority('TEACHER')")
    @PostMapping
    public ResponseEntity<?> createRequest(@RequestBody Request request) {
        // Set authenticated teacher
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        userRepository.findByUsername(username).ifPresent(request::setTeacher);
        
        // Set first available worker
        userRepository.findAll().stream()
            .filter(u -> u.getRoles().contains("WORKER"))
            .findFirst()
            .ifPresent(request::setWorker);
        if (request.getItem() != null && request.getItem().getId() != null) {
            itemRepository.findById(request.getItem().getId()).ifPresent(request::setItem);
        }
        Request saved = requestRepository.save(request);


        return ResponseEntity.ok(saved);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'WORKER')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateRequestStatus(@PathVariable Long id, @RequestBody Request request) {
        return requestRepository.findById(id).map(existing -> {
            existing.setStatus(request.getStatus());
            Request updated = requestRepository.save(existing);
            return ResponseEntity.ok(updated);
        }).orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAuthority('TEACHER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRequest(@PathVariable Long id) {
        requestRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
