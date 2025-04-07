package com.inventory.service;

import java.util.List;
import java.util.Optional;

import com.inventory.model.User;

public interface IUserService {
    User createUser(User user);
    Optional<User> getUserById(Long id);
    Optional<User> getUserByUsername(String username);
    List<User> getAllUsers();
    User updateUser(Long id, User updatedUser);
    void deleteUser(Long id);
}
