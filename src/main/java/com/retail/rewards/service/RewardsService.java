package com.retail.rewards.service;

import com.retail.rewards.dao.TransactionDao;
import com.retail.rewards.model.RewardSummary;
import com.retail.rewards.model.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RewardsService {

    @Autowired
    private TransactionDao transactionDao;

    public void setTransactionDao(TransactionDao transactionDao) {
        this.transactionDao = transactionDao;
    }

    public RewardSummary calculateRewards(String customerId, LocalDate start, LocalDate end) {
        List<Transaction> transactions = transactionDao.getTransactions(customerId, start, end);

        Map<Month, Integer> monthlyPoints = new HashMap<>();
        int totalPoints = 0;

        for (Transaction tx : transactions) {
            int points = calculatePoints(tx.getAmount());
            totalPoints += points;
            monthlyPoints.merge(tx.getDate().getMonth(), points, Integer::sum);
        }

        return new RewardSummary(customerId, monthlyPoints, totalPoints, transactions);
    }

    public List<Transaction> getAllTransactions() {
        return transactionDao.getAllTransactions();
    }


    public Transaction saveTransaction(Transaction transaction) {
        return transactionDao.addTransaction(transaction);
    }

    public List<Transaction> getTransactionsByCustomerId(String customerId) {
        return transactionDao.getTransactionsByCustomerId(customerId);
    }

    private int calculatePoints(double amount) {
        int points = 0;
        if (amount > 100) {
            points += (int) ((amount - 100) * 2) + 50; // 50 points for $50-$100
        } else if (amount > 50) {
            points += (int) (amount - 50);
        }
        return points;
    }
}
