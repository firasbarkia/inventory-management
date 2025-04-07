package com.inventory.security;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                System.out.println("JWT Filter: Token received: " + token);
                String username = jwtUtil.extractUsername(token);
                System.out.println("JWT Filter: Extracted username: " + username);
                Set<String> roles = jwtUtil.extractRoles(token);
                System.out.println("JWT Filter: Extracted roles: " + roles);
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    username,
                                    null,
                                    roles.stream().map(org.springframework.security.core.authority.SimpleGrantedAuthority::new).collect(Collectors.toSet())
                            );
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    System.out.println("JWT Filter: Authentication set for user: " + username);
                }
            } catch (Exception e) {
                System.out.println("JWT Filter: Invalid token - " + e.getMessage());
            }
        } else {
            System.out.println("JWT Filter: No Bearer token found");
        }
        filterChain.doFilter(request, response);
    }
}
