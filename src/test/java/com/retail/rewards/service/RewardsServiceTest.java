package com.retail.rewards.service;

import com.retail.rewards.dao.TransactionDao;
import com.retail.rewards.model.RewardSummary;
import com.retail.rewards.model.Transaction;
import com.retail.rewards.util.RewardsCalculator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.Month;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RewardsServiceTest {

    @Mock
    private TransactionDao transactionDao;

    @Mock
    private RewardsCalculator calculator;

    @InjectMocks
    private RewardsService rewardsService;

    @Test
    @DisplayName("Should successfully calculate rewards when a customer has transactions in multiple months")
    void calculateRewards_WithValidTransactions_ReturnsCorrectSummary() {
        // Arrange
        String customerId = "CUST1";
        LocalDate start = LocalDate.of(2026, 1, 1);
        LocalDate end = LocalDate.of(2026, 3, 31);

        Transaction tx1 = new Transaction(1L, "CUST1", 120.0, LocalDate.of(2026, 1, 15));
        Transaction tx2 = new Transaction(2L, "CUST1", 75.0, LocalDate.of(2026, 2, 10));
        Transaction tx3 = new Transaction(3L, "CUST1", 200.0, LocalDate.of(2026, 3, 5));
        List<Transaction> transactions = List.of(tx1, tx2, tx3);

        // Mock DAO behavior
        when(transactionDao.getTransactions(customerId, start, end)).thenReturn(transactions);

        // Mock Calculator logic to mirror the production rules
        when(calculator.calculatePoints(120.0)).thenReturn(90);
        when(calculator.calculatePoints(75.0)).thenReturn(25);
        when(calculator.calculatePoints(200.0)).thenReturn(250);

        // Act
        RewardSummary summary = rewardsService.calculateRewards(customerId, start, end);

        // Assert
        assertNotNull(summary, "Reward summary should not be null");
        assertEquals(customerId, summary.getCustomerId(), "Customer ID should match");
        assertEquals(transactions, summary.getTransactions(), "Transaction list should match original data");

        // Total points verification: 90 + 25 + 250 = 365
        assertEquals(365, summary.getTotalPoints(), "Total points should be the sum of all transaction points");

        // Monthly points distribution verification
        assertEquals(3, summary.getMonthlyPoints().size(), "Should contain entries for 3 distinct months");
        assertEquals(90, summary.getMonthlyPoints().get(Month.JANUARY), "January points mismatch");
        assertEquals(25, summary.getMonthlyPoints().get(Month.FEBRUARY), "February points mismatch");
        assertEquals(250, summary.getMonthlyPoints().get(Month.MARCH), "March points mismatch");

        // Verify interactions with dependencies to ensure full execution path coverage
        verify(transactionDao, times(1)).getTransactions(customerId, start, end);
        verify(calculator, times(1)).calculatePoints(120.0);
        verify(calculator, times(1)).calculatePoints(75.0);
        verify(calculator, times(1)).calculatePoints(200.0);
    }

    @Test
    @DisplayName("Should successfully return zero points and an empty breakdown when no transactions exist")
    void calculateRewards_WithNoTransactions_ReturnsEmptySummary() {
        // Arrange
        String customerId = "CUST_EMPTY";
        LocalDate start = LocalDate.of(2026, 1, 1);
        LocalDate end = LocalDate.of(2026, 3, 31);

        when(transactionDao.getTransactions(customerId, start, end)).thenReturn(Collections.emptyList());

        // Act
        RewardSummary summary = rewardsService.calculateRewards(customerId, start, end);

        // Assert
        assertNotNull(summary);
        assertEquals(customerId, summary.getCustomerId());
        assertTrue(summary.getTransactions().isEmpty(), "Transactions collection should be empty");
        assertEquals(0, summary.getTotalPoints(), "Total points must be zero");
        assertTrue(summary.getMonthlyPoints().isEmpty(), "Monthly points breakdown must be empty");

        // Verify interaction boundaries
        verify(transactionDao, times(1)).getTransactions(customerId, start, end);
        verifyNoInteractions(calculator); // Calculator branch shouldn't be executed for an empty loop
    }

    @Test
    @DisplayName("Should properly aggregate point subtotals when multiple transactions occur in the same month")
    void calculateRewards_WithMultipleTransactionsInSameMonth_AggregatesPoints() {
        // Arrange
        String customerId = "CUST1";
        LocalDate start = LocalDate.of(2026, 1, 1);
        LocalDate end = LocalDate.of(2026, 1, 31);

        Transaction tx1 = new Transaction(1L, "CUST1", 60.0, LocalDate.of(2026, 1, 10));
        Transaction tx2 = new Transaction(2L, "CUST1", 110.0, LocalDate.of(2026, 1, 20));
        List<Transaction> transactions = List.of(tx1, tx2);

        when(transactionDao.getTransactions(customerId, start, end)).thenReturn(transactions);
        when(calculator.calculatePoints(60.0)).thenReturn(10);
        when(calculator.calculatePoints(110.0)).thenReturn(70);

        // Act
        RewardSummary summary = rewardsService.calculateRewards(customerId, start, end);

        // Assert
        assertNotNull(summary);
        assertEquals(80, summary.getTotalPoints(), "Total combined points mismatch (10 + 70)");
        assertEquals(1, summary.getMonthlyPoints().size(), "Breakdown should only contain one month layer");
        assertEquals(80, summary.getMonthlyPoints().get(Month.JANUARY), "January points aggregation failed");

        verify(transactionDao, times(1)).getTransactions(customerId, start, end);
        verify(calculator, times(1)).calculatePoints(60.0);
        verify(calculator, times(1)).calculatePoints(110.0);
    }
}
