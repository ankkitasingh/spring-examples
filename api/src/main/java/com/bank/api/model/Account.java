package com.bank.api.model;

import java.math.BigDecimal;

import jakarta.persistence.*;

@Entity
@Table(name = "Account")
public class Account {
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false, unique = true)
	private String owner;
	
	@Column(nullable = false, precision = 19, scale = 2)
	private BigDecimal balance;

	protected Account() {
	}

	public Account(String owner, BigDecimal balance) {
		this.owner = owner;
		this.balance = balance;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}
	
	
	public void withdraw(BigDecimal amount) {
	    if (amount.signum() <= 0) throw new IllegalArgumentException("amount > 0");
	    if (balance.compareTo(amount) < 0) throw new IllegalStateException("insufficient funds");
	    balance = balance.subtract(amount);
	  }
	  public void deposit(BigDecimal amount) {
	    if (amount.signum() <= 0) throw new IllegalArgumentException("amount > 0");
	    balance = balance.add(amount);
	  }
	
	

}
