package com.inventory.security;

import io.jsonwebtoken.*;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Set;

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

    public Jws<Claims> validateToken(String token) {
        return Jwts.parser()
                .setSigningKey(secret.getBytes())
                .parseClaimsJws(token);
    }

    public String getUsername(String token) {
        return validateToken(token).getBody().getSubject();
    }

    public Set<String> getRoles(String token) {
        return validateToken(token).getBody().get("roles", Set.class);
    }
}
