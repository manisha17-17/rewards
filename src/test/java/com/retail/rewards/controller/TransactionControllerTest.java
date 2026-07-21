package com.retail.rewards.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.retail.rewards.model.Transaction;
import com.retail.rewards.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionController.class)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TransactionService transactionService;

    @Test
    void shouldCreateTransaction() throws Exception {

        Transaction transaction = new Transaction(
                1L,
                "CUST1",
                new BigDecimal("120.00"),
                LocalDate.of(2026, 1, 10)
        );

        when(transactionService.saveTransaction(any(Transaction.class)))
                .thenReturn(transaction);

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transaction)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.customerId").value("CUST1"));
    }

    @Test
    void shouldReturnTransactionsByCustomer() throws Exception {

        Transaction transaction = new Transaction(
                1L,
                "CUST1",
                new BigDecimal("120.00"),
                LocalDate.now()
        );

        when(transactionService.getTransactionsByCustomerId("CUST1"))
                .thenReturn(List.of(transaction));

        mockMvc.perform(get("/api/transactions/CUST1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].customerId").value("CUST1"));
    }

    @Test
    void shouldReturnNotFoundForUnknownCustomer() throws Exception {

        when(transactionService.getTransactionsByCustomerId("UNKNOWN"))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/transactions/UNKNOWN"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnAllTransactions() throws Exception {

        Transaction transaction = new Transaction(
                1L,
                "CUST1",
                new BigDecimal("120.00"),
                LocalDate.now()
        );

        when(transactionService.getAllTransactions())
                .thenReturn(List.of(transaction));

        mockMvc.perform(get("/api/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].customerId").value("CUST1"));
    }

    @Test
    void shouldReturnBadRequestWhenValidationFails() throws Exception {

        Transaction transaction = new Transaction(
                null,
                "",
                new BigDecimal("-5"),
                null
        );

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transaction)))
                .andExpect(status().isBadRequest());
    }
}