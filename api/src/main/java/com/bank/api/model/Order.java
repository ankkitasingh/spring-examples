package com.bank.api.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name="orders")
public class Order {

	  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	  private Long id;

	  @ManyToOne(optional=false, fetch=FetchType.LAZY)
	  @JoinColumn(name="user_id")
	  private User user;

	  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
	  private List<OrderItem> items = new ArrayList<>();

	  @Column(name="created_at", nullable=false)
	  private Instant createdAt = Instant.now();

	  protected Order() {}
	  public Order(User user){ this.user=user; }

	  public void addItem(OrderItem item){ items.add(item); item.setOrder(this); }
	  public Long getId(){return id;} public User getUser(){return user;}
	  public List<OrderItem> getItems(){return items;}
	  
}
