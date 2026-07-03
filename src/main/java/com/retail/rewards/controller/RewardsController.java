package com.retail.rewards.controller;

import com.retail.rewards.model.RewardSummary;
import com.retail.rewards.model.Transaction;
import com.retail.rewards.service.RewardsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/rewards")
public class RewardsController {

    @Autowired
    private RewardsService service;

    @GetMapping("/{customerId}")
    public ResponseEntity<RewardSummary> getRewards(
            @PathVariable String customerId,
            @RequestParam String startDate,
            @RequestParam String endDate) {

        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);

        RewardSummary summary = service.calculateRewards(customerId, start, end);
        return ResponseEntity.ok(summary);
    }

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

    @GetMapping("/customers")
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        List<Transaction> transactions = service.getAllTransactions();
        return ResponseEntity.ok(transactions);
    }
}
