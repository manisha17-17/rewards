package com.retail.rewards.service;

import com.retail.rewards.dao.TransactionRepository;
import com.retail.rewards.model.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class TransactionService {

    private final TransactionRepository repository;

    public TransactionService(TransactionRepository repository) {
        this.repository = repository;
    }

    public List<Transaction> getAllTransactions() {
        log.info("Fetching all transactions");
        return repository.findAll();
    }

    public Transaction saveTransaction(Transaction transaction) {

        log.info("Saving transaction for customer: {}", transaction.getCustomerId());

        // Only needed if client sends an ID
        if (transaction.getId() != null &&
                repository.existsById(transaction.getId())) {

            throw new IllegalArgumentException(
                    "Transaction with ID " + transaction.getId() + " already exists.");
        }

        return repository.save(transaction);
    }

    public List<Transaction> getTransactionsByCustomerId(String customerId) {

        log.info("Fetching transactions for customer: {}", customerId);

        return repository.findByCustomerId(customerId);
    }
}