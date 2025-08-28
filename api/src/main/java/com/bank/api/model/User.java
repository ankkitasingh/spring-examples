package com.bank.api.model;

import jakarta.persistence.*;

@Entity
@Table(name = "userss")
public class User {
	
	  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	  private Long id;
	  @Column(nullable=false, unique=true) private String email;
	  @Column(nullable=false) private String name;

	  protected User() {}
	  public User(String email, String name) { this.email=email; this.name=name; }

	  public Long getId() { return id; }
	  public String getEmail() { return email; }
	  public String getName() { return name; }

}
