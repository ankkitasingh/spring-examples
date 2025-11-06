package com.securityy.data.service;

import java.util.stream.Collectors;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.securityy.data.entity.User;
import com.securityy.data.repository.UserRepository;

@Service
public class MyUserDetailService implements UserDetailsService {

    private final UserRepository repo;

    public MyUserDetailService(UserRepository repo) { this.repo = repo; }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User appUser = repo.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        // Map "ADMIN" -> "ROLE_ADMIN" for hasRole("ADMIN")
        var authorities = appUser.getRoles().stream()
            .map(r -> new SimpleGrantedAuthority("ROLE_" + r))
            .collect(Collectors.toSet());

        return new org.springframework.security.core.userdetails.User(
            appUser.getUsername(),
            appUser.getPassword(),   // Day 3 ensures this is an encoded hash
            authorities
        );
    }
}


