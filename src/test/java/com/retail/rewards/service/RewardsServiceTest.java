package com.retail.rewards.service;

import com.retail.rewards.dao.TransactionRepository;
import com.retail.rewards.model.RewardSummary;
import com.retail.rewards.model.Transaction;
import com.retail.rewards.util.RewardsCalculator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RewardsServiceTest {

    @Mock
    private TransactionRepository repository;

    @Mock
    private RewardsCalculator calculator;

    @InjectMocks
    private RewardsService rewardsService;

    private List<Transaction> transactions;

    @BeforeEach
    void setup() {

        transactions = List.of(
                new Transaction(
                        1L,
                        "CUST1",
                        new BigDecimal("120.00"),
                        LocalDate.of(2026,1,15)
                ),
                new Transaction(
                        2L,
                        "CUST1",
                        new BigDecimal("80.00"),
                        LocalDate.of(2026,2,10)
                )
        );
    }

    @Test
    void calculateRewards_ShouldReturnRewardSummary() {

        when(repository.findByCustomerIdAndDateBetween(
                "CUST1",
                LocalDate.of(2026,1,1),
                LocalDate.of(2026,2,28)))
                .thenReturn(transactions);

        when(calculator.calculatePoints(new BigDecimal("120.00"))).thenReturn(90);
        when(calculator.calculatePoints(new BigDecimal("80.00"))).thenReturn(30);

        RewardSummary summary = rewardsService.calculateRewards(
                "CUST1",
                LocalDate.of(2026,1,1),
                LocalDate.of(2026,2,28));

        assertNotNull(summary);
        assertEquals("CUST1", summary.getCustomerId());
        assertEquals(120, summary.getTotalPoints());

        Map<String,Integer> monthly = summary.getMonthlyPoints();

        assertEquals(90, monthly.get("Jan-2026"));
        assertEquals(30, monthly.get("Feb-2026"));

        assertEquals(2, summary.getTransactions().size());
    }

    @Test
    void calculateRewards_NoTransactions() {

        when(repository.findByCustomerIdAndDateBetween(
                "CUST1",
                LocalDate.of(2026,1,1),
                LocalDate.of(2026,2,28)))
                .thenReturn(List.of());

        RewardSummary summary = rewardsService.calculateRewards(
                "CUST1",
                LocalDate.of(2026,1,1),
                LocalDate.of(2026,2,28));

        assertEquals(0, summary.getTotalPoints());
        assertTrue(summary.getMonthlyPoints().isEmpty());
        assertTrue(summary.getTransactions().isEmpty());
    }

    @Test
    void calculateAllRewards_ShouldReturnAllCustomersRewards() {

        List<Transaction> all = List.of(
                new Transaction(
                        1L,
                        "CUST1",
                        new BigDecimal("120"),
                        LocalDate.of(2026,1,15)
                ),
                new Transaction(
                        2L,
                        "CUST2",
                        new BigDecimal("130"),
                        LocalDate.of(2026,1,20)
                )
        );

        when(repository.findByDateBetween(
                LocalDate.of(2026,1,1),
                LocalDate.of(2026,1,31)))
                .thenReturn(all);

        when(calculator.calculatePoints(new BigDecimal("120"))).thenReturn(90);
        when(calculator.calculatePoints(new BigDecimal("130"))).thenReturn(110);

        List<RewardSummary> summaries =
                rewardsService.calculateAllRewards(
                        LocalDate.of(2026,1,1),
                        LocalDate.of(2026,1,31));

        assertEquals(2, summaries.size());
    }
}