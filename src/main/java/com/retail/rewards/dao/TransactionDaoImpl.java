package com.retail.rewards.dao;


import com.retail.rewards.model.Transaction;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.*;

@Repository
public class TransactionDaoImpl implements TransactionDao {

    private final Map<String, List<Transaction>> transactionStore = new HashMap<>();

    @PostConstruct
    public void initData() {

        transactionStore.put("CUST1", new ArrayList<>(Arrays.asList(
                new Transaction(1L, "CUST1", 120.0, LocalDate.of(2026, 1, 15)),
                new Transaction(2L, "CUST1", 75.0, LocalDate.of(2026, 2, 10)),
                new Transaction(3L, "CUST1", 200.0, LocalDate.of(2026, 3, 5))
        )));

        transactionStore.put("CUST2", new ArrayList<>(Arrays.asList(
                new Transaction(4L, "CUST2", 95.0, LocalDate.of(2026, 1, 20)),
                new Transaction(5L, "CUST2", 130.0, LocalDate.of(2026, 2, 25))
        )));
    }

    @Override
    public Transaction addTransaction(Transaction transaction) {
        transactionStore
                .computeIfAbsent(transaction.getCustomerId(), k -> new ArrayList<>())
                .add(transaction);
        return transaction;
    }

    @Override
    public List<Transaction> getAllTransactions() {
        return transactionStore.values()
                .stream()
                .flatMap(List::stream)
                .toList();
    }

    @Override
    public List<Transaction> getAllTransactionsInDateRange(LocalDate start, LocalDate end) {
        return transactionStore.values()
                .stream()
                .flatMap(List::stream)
                .filter(tx -> !tx.getDate().isBefore(start) && !tx.getDate().isAfter(end))
                .toList();
    }

    @Override
    public List<Transaction> getTransactionsByCustomerId(String customerId) {
        return transactionStore.getOrDefault(customerId, Collections.emptyList());
    }

    @Override
    public List<Transaction> getTransactions(String customerId, LocalDate start, LocalDate end) {
        return transactionStore.getOrDefault(customerId, Collections.emptyList())
                .stream()
                .filter(tx -> !tx.getDate().isBefore(start) && !tx.getDate().isAfter(end))
                .toList();
    }
}
