package com.securityy.data.config;

import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.securityy.data.entity.User;
import com.securityy.data.repository.UserRepository;

@Configuration
public class DataInitializer {
	
	@Bean
    CommandLineRunner initUsers(UserRepository repo,PasswordEncoder encoder) {
        return args -> {
            if (repo.count() == 0) {
                repo.save(new User("alice", encoder.encode("password"), Set.of("USER")));
                repo.save(new User("bob",   encoder.encode("password"), Set.of("USER","ADMIN")));
            }
        };
    }
}
