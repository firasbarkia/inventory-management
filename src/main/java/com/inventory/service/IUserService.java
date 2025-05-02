package com.inventory.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.inventory.model.AccountStatus;
import com.inventory.model.User; // Import Set for roles

public interface IUserService {
    User createUser(User user);
    Optional<User> getUserById(Long id);
    Optional<User> getUserByUsername(String username);
    List<User> getAllUsers();
    List<User> getUsersByStatus(AccountStatus status); // Add method to find users by status
    User updateUser(Long id, User updatedUser);
    User approveUser(Long id, Set<String> roles); // Add method to approve user and assign roles
    void deleteUser(Long id);
}
