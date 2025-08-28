package com.bank.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.bank.api.model.Account;
import com.bank.api.repository.AccountRepository;
import com.bank.api.services.BankService;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/bank")
public class BankController {
	
  private final BankService bank;
  private final AccountRepository accounts;

  public BankController(BankService bank, AccountRepository accounts) {
    this.bank = bank; this.accounts = accounts;
  }

  @PostMapping("/accounts")
  public Account create(@RequestBody Map<String, String> req) {
    return accounts.save(new Account(req.get("owner"), new BigDecimal(req.get("balance"))));
  }

  @PostMapping("/transfer")
  public ResponseEntity<?> transfer(@RequestBody Map<String, String> req) {
    bank.transfer(
      Long.valueOf(req.get("fromId")),
      Long.valueOf(req.get("toId")),
      new BigDecimal(req.get("amount")),
      Boolean.parseBoolean(req.getOrDefault("failAfterDebit","false"))
    );
    return ResponseEntity.ok(Map.of("status","ok"));
  }
}
