package com.retail.rewards.service;

import com.retail.rewards.dao.TransactionRepository;
import com.retail.rewards.model.RewardSummary;
import com.retail.rewards.model.Transaction;
import com.retail.rewards.util.RewardsCalculator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RewardsService {

    private final TransactionRepository repository;
    private final RewardsCalculator calculator;

    public RewardsService(TransactionRepository repository,
                          RewardsCalculator calculator) {
        this.repository = repository;
        this.calculator = calculator;
    }

    public RewardSummary calculateRewards(String customerId,
                                          LocalDate startDate,
                                          LocalDate endDate) {

        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date must not be after end date.");
        }

        log.info("Calculating rewards for customer {} between {} and {}",
                customerId, startDate, endDate);

        List<Transaction> transactions =
                repository.findByCustomerIdAndDateBetween(
                        customerId,
                        startDate,
                        endDate);

        return buildRewardSummary(customerId, transactions);
    }

    public List<RewardSummary> calculateAllRewards(LocalDate startDate,
                                                   LocalDate endDate) {

        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date must not be after end date.");
        }

        log.info("Calculating rewards for all customers between {} and {}",
                startDate, endDate);

        List<Transaction> allTransactions =
                repository.findByDateBetween(startDate, endDate);

        Map<String, List<Transaction>> groupedTransactions =
                allTransactions.stream()
                        .collect(Collectors.groupingBy(Transaction::getCustomerId));

        List<RewardSummary> summaries = new ArrayList<>();

        for (Map.Entry<String, List<Transaction>> entry : groupedTransactions.entrySet()) {

            summaries.add(
                    buildRewardSummary(
                            entry.getKey(),
                            entry.getValue()
                    )
            );
        }

        return summaries;
    }

    private RewardSummary buildRewardSummary(String customerId,
                                             List<Transaction> transactions) {

        Map<String, Integer> monthlyPoints = new LinkedHashMap<>();
        int totalPoints = 0;

        for (Transaction transaction : transactions) {

            int points = calculator.calculatePoints(transaction.getAmount());

            totalPoints += points;

            YearMonth yearMonth = YearMonth.from(transaction.getDate());

            String monthKey =
                    yearMonth.getMonth()
                            .getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
                            + "-" + yearMonth.getYear();

            monthlyPoints.merge(monthKey, points, Integer::sum);
        }

        return new RewardSummary(
                customerId,
                monthlyPoints,
                totalPoints,
                transactions
        );
    }
}