package com.inventory.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration; // Import DisabledException
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.inventory.model.AccountStatus;
import com.inventory.repository.UserRepository;
import com.inventory.security.JwtAuthenticationFilter; // Import AccountStatus
import com.inventory.security.JwtUtil;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final UserRepository userRepository;

    public SecurityConfig(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> userRepository.findByUsername(username)
                .map(user -> {
                    if (user.getAccountStatus() == AccountStatus.PENDING) {
                        // Throw exception if account is pending
                        throw new DisabledException("User account is pending approval");
                    }
                    // Proceed with creating UserDetails if account is ACTIVE
                    return org.springframework.security.core.userdetails.User
                            .withUsername(user.getUsername())
                            .password(user.getPassword())
                            .authorities(user.getRoles().toArray(new String[0]))
                            // Add account status checks if needed for more granular control
                            // .accountLocked(user.getAccountStatus() == AccountStatus.INACTIVE)
                            // .disabled(user.getAccountStatus() != AccountStatus.ACTIVE)
                            .build();
                })
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtUtil jwtUtil, UserDetailsService userDetailsService) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/register", "/api/auth/login").permitAll()
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/items", "/api/items/**").permitAll() // Allow public read access to items
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/suppliers", "/api/suppliers/**").hasAnyAuthority("SUPPLIER", "ADMIN")// Allow public read access to suppliers
                // Only allow TEACHER or ADMIN to access request endpoints
                .requestMatchers("/api/requests", "/api/requests/**").hasAnyAuthority("TEACHER", "ADMIN")
                .requestMatchers("/api/admin/**").hasAuthority("ADMIN")
                .requestMatchers("/api/supplier/**").hasAuthority("SUPPLIER")
                .anyRequest().authenticated()
            )
            .addFilterBefore(new JwtAuthenticationFilter(jwtUtil, userDetailsService), UsernamePasswordAuthenticationFilter.class)
            .httpBasic(httpBasic -> {});
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
