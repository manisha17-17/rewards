package com.retail.rewards.dao;

import com.retail.rewards.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByCustomerId(String customerId);

    List<Transaction> findByCustomerIdAndDateBetween(
            String customerId,
            LocalDate start,
            LocalDate end);

    List<Transaction> findByDateBetween(
            LocalDate start,
            LocalDate end);

    boolean existsByCustomerId(String customerId);
}