package com.bank.api.model;

import java.time.Instant;

import jakarta.persistence.*;

@Entity
@Table(name = "audit_log")
public class AuditLog {
	
	  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	  private Long id;

	  @Column(nullable = false, length = 4000)
	  private String message;

	  @Column(name = "created_at", nullable = false)
	  private Instant createdAt = Instant.now();

	  protected AuditLog() {}
	  public AuditLog(String message) { this.message = message; }

}
