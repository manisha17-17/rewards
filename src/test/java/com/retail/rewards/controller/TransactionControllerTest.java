package com.retail.rewards.controller;

import com.retail.rewards.model.Transaction;
import com.retail.rewards.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransactionController.class)
public class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TransactionService service;

    @Test
    public void testAddTransaction_Success() throws Exception {
        // Arrange
        Transaction inputTx = new Transaction(null, "CUST1", 120.0, LocalDate.of(2026, 1, 15));
        Transaction savedTx = new Transaction(1L, "CUST1", 120.0, LocalDate.of(2026, 1, 15));

        Mockito.when(service.saveTransaction(any(Transaction.class))).thenReturn(savedTx);


        mockMvc.perform(post("/api/transactions/transaction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputTx)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.customerId").value("CUST1"))
                .andExpect(jsonPath("$.amount").value(120.0))
                .andExpect(jsonPath("$.date").value("2026-01-15"));
    }

    @Test
    public void testGetTransactionsByCustomerId_Success() throws Exception {
        // Arrange
        String customerId = "CUST1";
        List<Transaction> mockTxs = Arrays.asList(
                new Transaction(1L, "CUST1", 120.0, LocalDate.of(2026, 1, 15)),
                new Transaction(2L, "CUST1", 75.0, LocalDate.of(2026, 2, 10))
        );

        Mockito.when(service.getTransactionsByCustomerId(eq(customerId))).thenReturn(mockTxs);


        mockMvc.perform(get("/api/transactions/" + customerId + "/transactions")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].customerId").value("CUST1"))
                .andExpect(jsonPath("$[1].id").value(2));
    }

    @Test
    public void testGetTransactionsByCustomerId_NotFound() throws Exception {

        String customerId = "UNKNOWN";
        Mockito.when(service.getTransactionsByCustomerId(eq(customerId))).thenReturn(Collections.emptyList());


        mockMvc.perform(get("/api/transactions/" + customerId + "/transactions")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetAllTransactions_Success() throws Exception {

        List<Transaction> allTxs = Arrays.asList(
                new Transaction(1L, "CUST1", 120.0, LocalDate.of(2026, 1, 15)),
                new Transaction(4L, "CUST2", 95.0, LocalDate.of(2026, 1, 20))
        );

        Mockito.when(service.getAllTransactions()).thenReturn(allTxs);


        mockMvc.perform(get("/api/transactions/customers")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].customerId").value("CUST1"))
                .andExpect(jsonPath("$[1].customerId").value("CUST2"));
    }
}
