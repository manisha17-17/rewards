package com.retail.rewards.controller;

import com.retail.rewards.model.RewardSummary;
import com.retail.rewards.service.RewardsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/rewards")
public class RewardsController {

    @Autowired
    private RewardsService service;

    @GetMapping("/{customerId}")
    public ResponseEntity<RewardSummary> getRewards(
            @PathVariable String customerId,
            @RequestParam String startDate,
            @RequestParam String endDate) {

        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);

        RewardSummary summary = service.calculateRewards(customerId, start, end);
        return ResponseEntity.ok(summary);
    }


}
