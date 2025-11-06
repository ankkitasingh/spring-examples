package com.securityy.data.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.securityy.data.dtos.LoginDTOs.LoginRequest;
import com.securityy.data.dtos.LoginDTOs.LoginResponse;
import com.securityy.data.repository.UserRepository;
import com.securityy.data.util.JwtUtil;

@RestController
@RequestMapping("/api/securityy/auth")
public class AuthController {

	private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;
    private final UserRepository repo;

    public AuthController(AuthenticationManager authManager, JwtUtil jwtUtil, UserRepository repo) {
        this.authManager = authManager;
        this.jwtUtil = jwtUtil;
        this.repo = repo;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest req) {
        Authentication auth = authManager.authenticate(
            new UsernamePasswordAuthenticationToken(req.username(), req.password())
        );

        var user = repo.findByUsername(req.username()).orElseThrow();
        var roles = user.getRoles().toArray(new String[0]);
        String token = jwtUtil.generate(user.getUsername(), roles);

        long expiresInSeconds = 60L * 60L; // keep in sync with app.jwt.exp-mins
        return ResponseEntity.ok(new LoginResponse(token, expiresInSeconds));
    }
}
