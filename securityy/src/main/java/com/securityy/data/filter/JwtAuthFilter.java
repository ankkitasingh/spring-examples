package com.securityy.data.filter;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.securityy.data.util.JwtUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;

    public JwtAuthFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws java.io.IOException, jakarta.servlet.ServletException {
        String header = req.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            try {
                String username = jwtUtil.validateAndGetSubject(token);
                var roles = jwtUtil.getRoles(token);
                var auth = new UsernamePasswordAuthenticationToken(
                        username,
                        null,
                        Arrays.stream(roles)
                              .map(r -> new SimpleGrantedAuthority("ROLE_" + r))
                              .collect(Collectors.toSet()));
                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (Exception e) {
                // invalid token -> let the entry point handle as UNAUTHORIZED
            }
        }
        chain.doFilter(req, res);
    }
}