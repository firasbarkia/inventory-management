package com.inventory.controller;

import java.util.List;
import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.inventory.model.AccountStatus;
import com.inventory.model.User;
import com.inventory.service.IUserService;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/admin/users")
@PreAuthorize("hasAuthority('ADMIN')")
public class UserController {

    private final IUserService userService;

    public UserController(IUserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User user) {
		System.out.println("Received user: " + user);
		if (user.getPassword() == null || user.getPassword().isEmpty()) {
            return ResponseEntity.badRequest().body("Password cannot be empty");
        }
        try {
            userService.getUserByUsername(user.getUsername());
            return ResponseEntity.status(409).body("Username already exists");
        } catch (UsernameNotFoundException e) {
            User createdUser = userService.createUser(user);
            return ResponseEntity.ok(createdUser);
        }
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/pending")
    public ResponseEntity<List<User>> getPendingUsers() {
        return ResponseEntity.ok(userService.getUsersByStatus(AccountStatus.PENDING));
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        try {
            User updatedUser = userService.updateUser(id, user);
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<?> approveUser(@PathVariable Long id, @RequestBody Set<String> roles) {
        try {
            // Ensure roles are provided and not empty for approval
            if (roles == null || roles.isEmpty()) {
                 return ResponseEntity.badRequest().body("Roles cannot be empty for approval");
            }
            User approvedUser = userService.approveUser(id, roles);
            return ResponseEntity.ok(approvedUser);
        } catch (RuntimeException e) {
            // Handle user not found or other potential errors from the service
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
