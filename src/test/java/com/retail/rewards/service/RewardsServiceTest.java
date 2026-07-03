package com.retail.rewards.service;


import com.retail.rewards.dao.TransactionDao;
import com.retail.rewards.model.RewardSummary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
class RewardsServiceTest {

    private RewardsService rewardsService;

    @BeforeEach
    void setUp() {
        TransactionDao dao = new TransactionDao();
        dao.initData();
        rewardsService = new RewardsService();
        rewardsService.setTransactionDao(dao); // add setter for test injection
    }

    @Test
    void testPurchaseAbove100() {
        RewardSummary summary = rewardsService.calculateRewards("CUST1",
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 31));
        assertEquals(90, summary.getMonthlyPoints().get(java.time.Month.JANUARY));
    }

    @Test
    void testPurchaseBetween50And100() {
        RewardSummary summary = rewardsService.calculateRewards("CUST1",
                LocalDate.of(2026, 2, 1), LocalDate.of(2026, 2, 28));
        assertEquals(25, summary.getMonthlyPoints().get(java.time.Month.FEBRUARY));
    }

    @Test
    void testPurchaseBelow50() {
        RewardSummary summary = rewardsService.calculateRewards("CUST2",
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 31));
        // 95 → 45 points
        assertEquals(45, summary.getMonthlyPoints().get(java.time.Month.JANUARY));
    }

    @Test
    void testTotalPoints() {
        RewardSummary summary = rewardsService.calculateRewards("CUST1",
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 3, 31));
        assertEquals(365, summary.getTotalPoints());
    }
}