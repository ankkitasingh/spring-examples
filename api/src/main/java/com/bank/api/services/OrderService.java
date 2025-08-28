package com.bank.api.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bank.api.model.Order;
import com.bank.api.model.OrderItem;
import com.bank.api.repository.OrderRepository;
import com.bank.api.repository.ProductRepository;
import com.bank.api.repository.UserRepository;

@Service
public class OrderService {
	
	
	private final OrderRepository orders;
	  private final UserRepository users;
	  private final ProductRepository products;

	  public OrderService(OrderRepository orders, UserRepository users, ProductRepository products) {
	    this.orders=orders; this.users=users; this.products=products;
	  }

	  @Transactional
	  public Long createOrder(String userEmail, List<Long> productIds) {
	    var user = users.findByEmail(userEmail).orElseThrow();
	    var order = new Order(user);
	    for (Long pid : productIds) {
	      var p = products.findById(pid).orElseThrow();
	      order.addItem(new OrderItem(p, 1, p.getPrice()));
	    }
	    return orders.save(order).getId();
	  }

	  @Transactional(readOnly = true)
	  public List<Order> getOrdersForUser(Long userId) {
	    return orders.findOrdersWithItemsForUser(userId);
	  }

}
