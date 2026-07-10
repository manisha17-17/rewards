package com.retail.rewards.service;


import com.retail.rewards.dao.TransactionDao;
import com.retail.rewards.model.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionService {

    @Autowired
    private TransactionDao transactionDao;


    public List<Transaction> getAllTransactions() {
        return transactionDao.getAllTransactions();
    }


    public Transaction saveTransaction(Transaction transaction) {
        return transactionDao.addTransaction(transaction);
    }

    public List<Transaction> getTransactionsByCustomerId(String customerId) {
        return transactionDao.getTransactionsByCustomerId(customerId);
    }
}
