package com.bank.api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bank.api.model.User;

public interface UserRepository extends JpaRepository<User,Long> {

	Optional<User> findByEmail(String email);
}
