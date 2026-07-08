package com.retail.rewards.dao;

import com.retail.rewards.model.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TransactionDaoTest {

    private TransactionDaoImpl dao;

    @BeforeEach
    void setUp() {
        dao = new TransactionDaoImpl();
        dao.initData(); // populate with sample data
    }

    @Test
    void testGetAllTransactions() {
        List<Transaction> all = dao.getAllTransactions();
        assertEquals(5, all.size(), "Should return 5 transactions in total");
    }

    @Test
    void testGetTransactionsByCustomerId() {
        List<Transaction> cust1Tx = dao.getTransactionsByCustomerId("CUST1");
        assertEquals(3, cust1Tx.size(), "CUST1 should have 3 transactions");

        List<Transaction> cust2Tx = dao.getTransactionsByCustomerId("CUST2");
        assertEquals(2, cust2Tx.size(), "CUST2 should have 2 transactions");

        List<Transaction> cust3Tx = dao.getTransactionsByCustomerId("CUST3");
        assertTrue(cust3Tx.isEmpty(), "Unknown customer should return empty list");
    }

    @Test
    void testGetTransactionsWithinDateRange() {
        LocalDate start = LocalDate.of(2026, 2, 1);
        LocalDate end = LocalDate.of(2026, 2, 28);

        List<Transaction> febTx = dao.getTransactions("CUST1", start, end);
        assertEquals(1, febTx.size(), "CUST1 should have 1 transaction in Feb 2026");
        assertEquals(75.0, febTx.get(0).getAmount());
    }

    @Test
    void testAddTransaction() {
        Transaction newTx = new Transaction(6L, "CUST1", 150.0, LocalDate.of(2026, 4, 1));
        dao.addTransaction(newTx);

        List<Transaction> cust1Tx = dao.getTransactionsByCustomerId("CUST1");
        assertEquals(4, cust1Tx.size(), "CUST1 should now have 4 transactions");
        assertTrue(cust1Tx.contains(newTx), "New transaction should be present");
    }

    @Test
    void testAddTransactionForNewCustomer() {
        Transaction newTx = new Transaction(7L, "CUST3", 99.0, LocalDate.of(2026, 5, 1));
        dao.addTransaction(newTx);

        List<Transaction> cust3Tx = dao.getTransactionsByCustomerId("CUST3");
        assertEquals(1, cust3Tx.size(), "CUST3 should now have 1 transaction");
        assertEquals(99.0, cust3Tx.get(0).getAmount());
    }
}
