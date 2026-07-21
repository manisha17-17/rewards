package com.retail.rewards.service;

import com.retail.rewards.dao.TransactionRepository;
import com.retail.rewards.model.Transaction;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository repository;

    @InjectMocks
    private TransactionService service;

    @Test
    void saveTransaction_ShouldSave() {

        Transaction transaction = new Transaction(
                1L,
                "CUST1",
                new BigDecimal("120"),
                LocalDate.now());

        when(repository.save(transaction))
                .thenReturn(transaction);

        Transaction saved = service.saveTransaction(transaction);

        assertNotNull(saved);
        assertEquals("CUST1", saved.getCustomerId());

        verify(repository).save(transaction);
    }

    @Test
    void getAllTransactions_ShouldReturnList() {

        List<Transaction> list = List.of(
                new Transaction(
                        1L,
                        "CUST1",
                        new BigDecimal("120"),
                        LocalDate.now())
        );

        when(repository.findAll()).thenReturn(list);

        List<Transaction> result = service.getAllTransactions();

        assertEquals(1, result.size());

        verify(repository).findAll();
    }

    @Test
    void getTransactionsByCustomerId_ShouldReturnCustomerTransactions() {

        List<Transaction> list = List.of(
                new Transaction(
                        1L,
                        "CUST1",
                        new BigDecimal("120"),
                        LocalDate.now())
        );

        when(repository.findByCustomerId("CUST1"))
                .thenReturn(list);

        List<Transaction> result =
                service.getTransactionsByCustomerId("CUST1");

        assertEquals(1, result.size());
        assertEquals("CUST1", result.get(0).getCustomerId());

        verify(repository).findByCustomerId("CUST1");
    }

    @Test
    void getTransactionsByCustomerId_ShouldReturnEmptyList() {

        when(repository.findByCustomerId("UNKNOWN"))
                .thenReturn(List.of());

        List<Transaction> result =
                service.getTransactionsByCustomerId("UNKNOWN");

        assertTrue(result.isEmpty());

        verify(repository).findByCustomerId("UNKNOWN");
    }
}