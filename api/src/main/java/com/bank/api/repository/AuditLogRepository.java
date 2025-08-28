package com.bank.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bank.api.model.AuditLog;

public interface AuditLogRepository extends JpaRepository<AuditLog,Long>{

}
