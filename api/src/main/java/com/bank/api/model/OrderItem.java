package com.bank.api.model;

import java.math.BigDecimal;

import jakarta.persistence.*;

@Entity
@Table(name="order_item")
public class OrderItem {
	
	  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	  private Long id;

	  @ManyToOne(optional=false, fetch=FetchType.LAZY)
	  @JoinColumn(name="order_id")
	  private Order order;

	  @ManyToOne(optional=false, fetch=FetchType.LAZY)
	  @JoinColumn(name="product_id")
	  private Product product;

	  private int quantity;
	  @Column(name="unit_price", precision=19, scale=2)
	  private BigDecimal unitPrice;

	  protected OrderItem() {}
	  public OrderItem(Product product, int quantity, BigDecimal unitPrice){
	    this.product=product; this.quantity=quantity; this.unitPrice=unitPrice;
	  }

	  void setOrder(Order order){ this.order = order; }
	  public Product getProduct(){ return product; }
	  public int getQuantity(){ return quantity; }
	  public BigDecimal getUnitPrice(){ return unitPrice; }

}
