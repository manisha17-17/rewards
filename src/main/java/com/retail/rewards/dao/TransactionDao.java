package com.retail.rewards.dao;

import com.retail.rewards.model.Transaction;

import java.time.LocalDate;
import java.util.List;

public interface TransactionDao {

    Transaction addTransaction(Transaction transaction);

    List<Transaction> getAllTransactions();

    List<Transaction> getTransactionsByCustomerId(String customerId);

    List<Transaction> getTransactions(String customerId, LocalDate start, LocalDate end);

    List<Transaction> getAllTransactionsInDateRange(LocalDate start, LocalDate end);
}
