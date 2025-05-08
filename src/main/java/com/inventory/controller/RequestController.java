package com.inventory.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.inventory.model.Request;
import com.inventory.model.RequestDTO;
import com.inventory.model.User;
import com.inventory.repository.ItemRepository;
import com.inventory.repository.RequestRepository;
import com.inventory.repository.UserRepository;
import com.inventory.service.NotificationService;
@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/requests")
public class RequestController {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final NotificationService notificationService;

    public RequestController(RequestRepository requestRepository, UserRepository userRepository, ItemRepository itemRepository, NotificationService notificationService) {
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.notificationService = notificationService;
    }

    @GetMapping
    public ResponseEntity<List<RequestDTO>> getAllRequests() {
        List<RequestDTO> requests = requestRepository.findAll().stream()
            .map(RequestDTO::new)
            .collect(Collectors.toList());
        return ResponseEntity.ok(requests);
    }
    @PreAuthorize("hasAnyAuthority('TEACHER', 'WORKER')")
    @GetMapping("/{id}")
    public ResponseEntity<RequestDTO> getRequestById(@PathVariable Long id) {
        return requestRepository.findById(id)
                .map(RequestDTO::new)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAnyAuthority('TEACHER')")
    @PostMapping
    public ResponseEntity<RequestDTO> createRequest(@RequestBody Request request) {
        // Set authenticated teacher
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        userRepository.findByUsername(username).ifPresent(request::setTeacher);
        
        // Set first available worker
        List<User> workers = userRepository.findByRoles("WORKER");
        if (!workers.isEmpty()) {
            request.setWorker(workers.get(0));
        }
        if (request.getItem() != null && request.getItem().getId() != null) {
            itemRepository.findById(request.getItem().getId()).ifPresent(request::setItem);
        }
        Request saved = requestRepository.save(request);


        notificationService.createWorkerNotification(saved);
        return ResponseEntity.ok(new RequestDTO(saved));
    }

    @PreAuthorize("hasAnyAuthority('WORKER')")
    @PutMapping("/{id}")
    public ResponseEntity<RequestDTO> updateRequestStatus(@PathVariable Long id, @RequestBody Request request) {
        return requestRepository.findById(id).map(existing -> {
            existing.setStatus(request.getStatus());
            Request updated = requestRepository.save(existing);
            return ResponseEntity.ok(new RequestDTO(updated));
        }).orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAuthority('TEACHER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRequest(@PathVariable Long id) {
        requestRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
