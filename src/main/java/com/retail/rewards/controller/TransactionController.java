package com.retail.rewards.controller;

import com.retail.rewards.model.Transaction;
import com.retail.rewards.service.TransactionService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@Validated
@Slf4j
public class TransactionController {

    private final TransactionService service;

    public TransactionController(TransactionService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Transaction> addTransaction(
            @Valid @RequestBody Transaction transaction) {

        log.info("Adding transaction for customer {}", transaction.getCustomerId());

        Transaction saved = service.saveTransaction(transaction);

        return ResponseEntity
                .created(URI.create("/api/transactions/" + saved.getId()))
                .body(saved);
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<List<Transaction>> getTransactionsByCustomerId(
            @PathVariable
            @NotBlank(message = "Customer ID is required")
            String customerId) {

        log.info("Fetching transactions for customer {}", customerId);

        List<Transaction> transactions =
                service.getTransactionsByCustomerId(customerId);

        if (transactions.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(transactions);
    }

    @GetMapping
    public ResponseEntity<List<Transaction>> getAllTransactions() {

        log.info("Fetching all transactions");

        return ResponseEntity.ok(service.getAllTransactions());
    }
}