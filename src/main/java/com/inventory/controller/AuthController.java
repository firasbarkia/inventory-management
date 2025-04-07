package com.inventory.controller;

import com.inventory.model.User;
import com.inventory.service.IUserService;
import com.inventory.security.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final IUserService userService;
    private final JwtUtil jwtUtil;

    public AuthController(IUserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) {
        User createdUser = userService.createUser(user);
        return ResponseEntity.ok(createdUser);
    }

    @GetMapping("/login")
    public ResponseEntity<String> login(Authentication authentication) {
        Set<String> roles = authentication.getAuthorities().stream()
                .map(auth -> auth.getAuthority())
                .collect(java.util.stream.Collectors.toSet());
        String token = jwtUtil.generateToken(authentication.getName(), roles);
        return ResponseEntity.ok(token);
    }
}
