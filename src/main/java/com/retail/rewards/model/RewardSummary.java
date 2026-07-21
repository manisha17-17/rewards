package com.retail.rewards.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RewardSummary {

    private String customerId;

    // Example: {"Jan-2026": 90, "Feb-2026": 25}
    private Map<String, Integer> monthlyPoints;

    private int totalPoints;

    private List<Transaction> transactions;
}