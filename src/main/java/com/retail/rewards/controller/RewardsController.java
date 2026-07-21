package com.retail.rewards.controller;

import com.retail.rewards.model.RewardSummary;
import com.retail.rewards.service.RewardsService;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/rewards")
@Validated
@Slf4j
public class RewardsController {

    private final RewardsService service;

    public RewardsController(RewardsService service) {
        this.service = service;
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<RewardSummary> getRewards(

            @PathVariable
            @NotBlank(message = "Customer ID is required")
            String customerId,

            @RequestParam LocalDate startDate,

            @RequestParam LocalDate endDate) {

        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException(
                    "Start date must not be after end date.");
        }

        log.info("Fetching rewards for customer {} from {} to {}",
                customerId, startDate, endDate);

        RewardSummary summary =
                service.calculateRewards(customerId, startDate, endDate);

        return ResponseEntity.ok(summary);
    }

    @GetMapping
    public ResponseEntity<List<RewardSummary>> getAllRewards(

            @RequestParam LocalDate startDate,

            @RequestParam LocalDate endDate) {

        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException(
                    "Start date must not be after end date.");
        }

        log.info("Fetching rewards for all customers from {} to {}",
                startDate, endDate);

        List<RewardSummary> summaries =
                service.calculateAllRewards(startDate, endDate);

        return ResponseEntity.ok(summaries);
    }
}