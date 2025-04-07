package com.inventory.controller;

import com.inventory.model.User;
import com.inventory.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.boot.test.web.client.TestRestTemplate;

import java.util.HashSet;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
class AuthControllerIT {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/auth";
    }

    @Test
    @Rollback
    void register_shouldCreateUser() {
        String url = baseUrl + "/register";

        String userJson = """
            {
                "username": "integrationUser",
                "password": "password",
                "email": "integration@example.com",
                "roles": ["TEACHER"]
            }
            """;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(userJson, headers);

        ResponseEntity<User> response = restTemplate.postForEntity(url, request, User.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        User createdUser = response.getBody();
        assertThat(createdUser).isNotNull();
        assertThat(createdUser.getUsername()).isEqualTo("integrationUser");
        assertThat(createdUser.getEmail()).isEqualTo("integration@example.com");

        // Verify user saved in DB
        User userInDb = userRepository.findByUsername("integrationUser").orElse(null);
        assertThat(userInDb).isNotNull();
        assertThat(userInDb.getEmail()).isEqualTo("integration@example.com");
    }

    @Test
    @Rollback
    void login_shouldReturnJwtToken() {
        // Prepare a user in DB
        User user = new User();
        user.setUsername("loginUser");
        user.setPassword("password"); // Assume password is stored encoded or adjust accordingly
        user.setEmail("login@example.com");
        user.setRoles(new HashSet<>(java.util.List.of("TEACHER")));
        userRepository.save(user);

        String url = baseUrl + "/login";

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth("loginUser", "password"); // If using HTTP Basic Auth, else adjust

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotBlank();
    }
}
