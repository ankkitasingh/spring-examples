package com.bank.api.services;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.bank.api.repository.AccountRepository;

@Service
public class BankService {
	
	
	private final AccountRepository accounts;
	private final AuditService audit;
	
	public BankService(AccountRepository accounts, AuditService audit) {
	    this.accounts = accounts; 
	    this.audit = audit;
	  }
	
	
	@Transactional(isolation = Isolation.READ_COMMITTED)
	  public void transfer(Long fromId, Long toId, BigDecimal amount, boolean failAfterDebit) {
	    var from = accounts.findById(fromId).orElseThrow();
	    var to   = accounts.findById(toId).orElseThrow();

	    from.withdraw(amount);
	    to.deposit(amount);

	    audit.log("Transferred %s from %s to %s".formatted(amount, from.getOwner(), to.getOwner()));

	    if (failAfterDebit) throw new RuntimeException("Simulated failure");
	  }

}
