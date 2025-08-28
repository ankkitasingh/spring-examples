package com.bank.api.model;

import java.math.BigDecimal;

import jakarta.persistence.*;

@Entity 
@Table(name="productss")
public class Product {
	
	  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	  private Long id;

	  @Column(nullable=false) private String name;
	  private String category;

	  @Column(nullable=false, precision=19, scale=2)
	  private BigDecimal price;

	  @Column(nullable=false) private boolean active = true;

	  protected Product() {}
	  public Product(String name, String category, BigDecimal price) {
	    this.name=name; this.category=category; this.price=price;
	  }

	  public Long getId(){return id;} public String getName(){return name;}
	  public String getCategory(){return category;} public BigDecimal getPrice(){return price;}
	  public boolean isActive(){return active;}


}
