package com.retail.rewards.controller;


import com.retail.rewards.model.Transaction;
import com.retail.rewards.service.RewardsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired
    private RewardsService service;

    @PostMapping("/transaction")
    public ResponseEntity<Transaction> addTransaction(@RequestBody Transaction transaction) {
        Transaction saved = service.saveTransaction(transaction);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/{customerId}/transactions")
    public ResponseEntity<List<Transaction>> getTransactionsByCustomerId(
            @PathVariable String customerId) {
        List<Transaction> transactions = service.getTransactionsByCustomerId(customerId);
        if (transactions.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(transactions);
    }
}
