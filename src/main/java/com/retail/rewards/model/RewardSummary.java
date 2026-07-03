package com.retail.rewards.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Month;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class RewardSummary {
    private String customerId;
    private Map<Month, Integer> monthlyPoints;
    private int totalPoints;
    private List<Transaction> transactions;
}