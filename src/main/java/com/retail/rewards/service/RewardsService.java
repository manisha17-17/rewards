package com.retail.rewards.service;

import com.retail.rewards.dao.TransactionDao;
import com.retail.rewards.model.RewardSummary;
import com.retail.rewards.model.Transaction;
import com.retail.rewards.util.RewardsCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RewardsService {

    @Autowired
    private TransactionDao transactionDao;

    @Autowired
    private RewardsCalculator calculator;


    public RewardSummary calculateRewards(String customerId, LocalDate start, LocalDate end) {
        List<Transaction> transactions = transactionDao.getTransactions(customerId, start, end);

        Map<Month, Integer> monthlyPoints = new HashMap<>();
        int totalPoints = 0;

        for (Transaction tx : transactions) {
            int points = calculator.calculatePoints(tx.getAmount());
            totalPoints += points;
            monthlyPoints.merge(tx.getDate().getMonth(), points, Integer::sum);
        }

        return new RewardSummary(customerId, monthlyPoints, totalPoints, transactions);
    }

    public List<RewardSummary> calculateAllRewards(LocalDate start, LocalDate end) {

        List<Transaction> allTransactions = transactionDao.getAllTransactionsInDateRange(start, end);


        Map<String, List<Transaction>> transactionsByCustomer = allTransactions.stream()
                .collect(Collectors.groupingBy(Transaction::getCustomerId));

        List<RewardSummary> summaries = new ArrayList<>();

        for (Map.Entry<String, List<Transaction>> entry : transactionsByCustomer.entrySet()) {
            String customerId = entry.getKey();
            List<Transaction> customerTransactions = entry.getValue();

            Map<Month, Integer> monthlyPoints = new HashMap<>();
            int totalPoints = 0;

            for (Transaction tx : customerTransactions) {
                int points = calculator.calculatePoints(tx.getAmount());
                totalPoints += points;
                monthlyPoints.merge(tx.getDate().getMonth(), points, Integer::sum);
            }

            summaries.add(new RewardSummary(customerId, monthlyPoints, totalPoints, customerTransactions));
        }

        return summaries;
    }


}
