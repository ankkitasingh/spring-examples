package com.bank.api.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bank.api.model.Order;
import com.bank.api.services.OrderService;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
  private final OrderService orders;
  public OrderController(OrderService orders) { this.orders = orders; }

  @PostMapping
  public Map<String, Object> create(@RequestBody Map<String, Object> body) {
    String email = (String) body.get("email");
    @SuppressWarnings("unchecked")
    List<Integer> pidInts = (List<Integer>) body.get("productIds");
    List<Long> pids = pidInts.stream().map(Integer::longValue).toList();
    Long id = orders.createOrder(email, pids);
    return Map.of("orderId", id);
  }

  @GetMapping("/user/{userId}")
  public List<Order> byUser(@PathVariable Long userId) {
    return orders.getOrdersForUser(userId);
  }
}
