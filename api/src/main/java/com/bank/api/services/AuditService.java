package com.bank.api.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.bank.api.model.AuditLog;
import com.bank.api.repository.AuditLogRepository;



@Service
public class AuditService {
	
	private final AuditLogRepository repo;
	
	public AuditService(AuditLogRepository repo) {
		this.repo = repo;
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	  public void log(String msg) { repo.save(new AuditLog(msg)); }
}
