package com.retail.rewards.service;

import com.retail.rewards.dao.TransactionDao;
import com.retail.rewards.model.Transaction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionDao transactionDao;

    @InjectMocks
    private TransactionService transactionService;

    @Test
    @DisplayName("Should successfully retrieve all transactions across the platform")
    void getAllTransactions_ReturnsCompleteList() {
        // Arrange
        Transaction tx1 = new Transaction(1L, "CUST1", 120.0, LocalDate.of(2026, 1, 15));
        Transaction tx2 = new Transaction(2L, "CUST2", 95.0, LocalDate.of(2026, 1, 20));
        List<Transaction> mockTransactions = List.of(tx1, tx2);

        when(transactionDao.getAllTransactions()).thenReturn(mockTransactions);

        // Act
        List<Transaction> result = transactionService.getAllTransactions();

        // Assert
        assertNotNull(result, "Resulting collection should not be null");
        assertEquals(2, result.size(), "Result size should match mock data size");
        assertEquals(mockTransactions, result, "Returned list should exactly match repository output");

        // Verify mock integration
        verify(transactionDao, times(1)).getAllTransactions();
    }

    @Test
    @DisplayName("Should pass through and return saved transaction object when storing a new record")
    void saveTransaction_PersistsAndReturnsTransaction() {
        // Arrange
        Transaction inputTx = new Transaction(null, "CUST1", 150.0, LocalDate.of(2026, 3, 22));
        Transaction savedTx = new Transaction(100L, "CUST1", 150.0, LocalDate.of(2026, 3, 22));

        when(transactionDao.addTransaction(inputTx)).thenReturn(savedTx);

        // Act
        Transaction result = transactionService.saveTransaction(inputTx);

        // Assert
        assertNotNull(result, "Saved transaction should not be null");
        assertEquals(100L, result.getId(), "Returned object should contain generated database ID");
        assertEquals("CUST1", result.getCustomerId(), "Customer ID should remain consistent");
        assertEquals(150.0, result.getAmount(), "Transaction amount should match input details");

        // Verify mock integration
        verify(transactionDao, times(1)).addTransaction(inputTx);
    }

    @Test
    @DisplayName("Should successfully retrieve specific transactions filtering by a given Customer ID")
    void getTransactionsByCustomerId_WithExistingId_ReturnsMatchingList() {
        // Arrange
        String customerId = "CUST1";
        Transaction tx = new Transaction(1L, customerId, 120.0, LocalDate.of(2026, 1, 15));
        List<Transaction> mockTransactions = List.of(tx);

        when(transactionDao.getTransactionsByCustomerId(customerId)).thenReturn(mockTransactions);

        // Act
        List<Transaction> result = transactionService.getTransactionsByCustomerId(customerId);

        // Assert
        assertNotNull(result, "Resulting collection should not be null");
        assertFalse(result.isEmpty(), "Result should contain items for the specified customer");
        assertEquals(1, result.size());
        assertEquals(customerId, result.get(0).getCustomerId(), "Fetched row customer mapping mismatch");

        // Verify mock integration
        verify(transactionDao, times(1)).getTransactionsByCustomerId(customerId);
    }

    @Test
    @DisplayName("Should return an empty list if no transactions exist for the requested Customer ID")
    void getTransactionsByCustomerId_WithNonExistentId_ReturnsEmptyList() {
        // Arrange
        String unknownCustomerId = "CUST_NONE";
        when(transactionDao.getTransactionsByCustomerId(unknownCustomerId)).thenReturn(Collections.emptyList());

        // Act
        List<Transaction> result = transactionService.getTransactionsByCustomerId(unknownCustomerId);

        // Assert
        assertNotNull(result, "Resulting collection should not be null even when empty");
        assertTrue(result.isEmpty(), "Collection should be empty for a target with zero history");

        // Verify mock integration
        verify(transactionDao, times(1)).getTransactionsByCustomerId(unknownCustomerId);
    }
}
