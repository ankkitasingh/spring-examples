package com.securityy.data.util;

import java.nio.charset.StandardCharsets;

import java.time.Instant;
import java.util.Date;

import javax.crypto.SecretKey;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

public class JwtUtil {
	
	
	private final SecretKey key;
    private final long expMillis;

    public JwtUtil(String secret, long expMinutes) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expMillis = expMinutes * 60_000;
    }

    public String generate(String username, String[] roles) {
        Instant now = Instant.now();
        return Jwts.builder()
            .subject(username)
            .issuedAt(Date.from(now))
            .expiration(Date.from(now.plusMillis(expMillis)))
            .claim("roles", roles)
            .signWith(key)
            .compact();
    }

    public String validateAndGetSubject(String token) {
        return Jwts.parser().verifyWith(key).build()
            .parseSignedClaims(token)
            .getPayload()
            .getSubject();
    }

    public String[] getRoles(String token) {
        var claims = Jwts.parser().verifyWith(key).build()
            .parseSignedClaims(token).getPayload();
        var list = (java.util.List<?>) claims.get("roles");
        return list.stream().map(Object::toString).toArray(String[]::new);
    }

}
