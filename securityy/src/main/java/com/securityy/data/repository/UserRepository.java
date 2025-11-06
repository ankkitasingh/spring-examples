package com.securityy.data.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.securityy.data.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}