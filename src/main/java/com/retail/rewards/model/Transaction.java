package com.retail.rewards.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {
    private Long id;
    private String customerId;
    private Double amount;
    private LocalDate date;
}