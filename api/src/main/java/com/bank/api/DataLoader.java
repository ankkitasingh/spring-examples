package com.bank.api;

import java.math.BigDecimal;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.bank.api.model.User;
import com.bank.api.model.Product;
import com.bank.api.repository.ProductRepository;
import com.bank.api.repository.UserRepository;

@Configuration
class DataLoader {
  @Bean
  CommandLineRunner seed(UserRepository users, ProductRepository products) {
    return args -> {
      if (users.count() == 0) {
        users.save(new User("alice@example.com", "Alice"));
        users.save(new User("bob@example.com", "Bob"));
      }
      if (products.count() == 0) {
        products.save(new Product("iPhone", "phones", new BigDecimal("999.00")));
        products.save(new Product("Pixel", "phones", new BigDecimal("799.00")));
        products.save(new Product("ThinkPad", "laptops", new BigDecimal("1599.00")));
      }
    };
  }
}