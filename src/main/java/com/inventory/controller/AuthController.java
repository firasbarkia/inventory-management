package com.inventory.controller;

import java.util.Map;
import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.inventory.model.User;
import com.inventory.security.JwtUtil;
import com.inventory.service.IUserService;
@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/auth")
public class AuthController {

    private final IUserService userService;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    public AuthController(IUserService userService, JwtUtil jwtUtil, AuthenticationManager authenticationManager, UserDetailsService userDetailsService) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        userService.createUser(user);
        User registeredUser = userService.getUserByUsername(user.getUsername());
        // Don't generate a token for newly registered users since they need admin approval
        return ResponseEntity.ok(Map.of("message", "Registration successful. Please wait for admin approval.",
                                       "accountStatus", registeredUser.getAccountStatus().toString()));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        try {
            String username = credentials.get("username");
            String password = credentials.get("password");
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
            Set<String> roles = authentication.getAuthorities().stream()
                    .map(auth -> auth.getAuthority())
                    .collect(java.util.stream.Collectors.toSet());
            String token = jwtUtil.generateToken(username, roles);
            User user = userService.getUserByUsername(username);
            return ResponseEntity.ok(Map.of("token", token, "accountStatus", user.getAccountStatus().toString(), "roles", roles));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(401).body("Invalid username or password");
        }
    }
}
