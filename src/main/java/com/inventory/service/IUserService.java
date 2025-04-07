package com.inventory.service;

import com.inventory.model.User;

import java.util.List;
import java.util.Optional;

public interface IUserService {
    User createUser(User user);
    Optional<User> getUserById(Long id);
    List<User> getAllUsers();
    User updateUser(Long id, User updatedUser);
    void deleteUser(Long id);
}
