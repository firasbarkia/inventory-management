package com.inventory.security;

import java.util.Date;
import java.util.Set;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtUtil {

    private final String secret = "mysecretkeymysecretkeymysecretkey"; // should be at least 256 bits
    private final long expirationMs = 86400000; // 1 day

    public String generateToken(String username, Set<String> roles) {
        return Jwts.builder()
                .setSubject(username)
                .claim("roles", roles)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(SignatureAlgorithm.HS256, secret.getBytes())
                .compact();
    }

    public String extractUsername(String token) {
        return getUsername(token);
    }

    public Set<String> extractRoles(String token) {
        return getRoles(token);
    }

    public Jws<Claims> validateToken(String token) {
        return Jwts.parser()
                .setSigningKey(secret.getBytes())
                .parseClaimsJws(token);
    }

    public String getUsername(String token) {
        return validateToken(token).getBody().getSubject();
    }

    public Set<String> getRoles(String token) {
        Object rolesObj = validateToken(token).getBody().get("roles");
        if (rolesObj instanceof java.util.Collection<?>) {
            java.util.Collection<?> rolesCollection = (java.util.Collection<?>) rolesObj;
            java.util.Set<String> rolesSet = new java.util.HashSet<>();
            for (Object role : rolesCollection) {
                rolesSet.add(role.toString());
            }
            return rolesSet;
        }
        return java.util.Collections.emptySet();
    }
}
