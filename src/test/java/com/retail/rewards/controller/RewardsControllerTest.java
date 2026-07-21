package com.retail.rewards.controller;

import com.retail.rewards.model.RewardSummary;
import com.retail.rewards.service.RewardsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RewardsController.class)
class RewardsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RewardsService rewardsService;

    @Test
    void shouldReturnCustomerRewards() throws Exception {

        RewardSummary summary = new RewardSummary(
                "CUST1",
                Map.of("Jan-2026", 90),
                90,
                List.of()
        );

        when(rewardsService.calculateRewards(
                "CUST1",
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 1, 31)))
                .thenReturn(summary);

        mockMvc.perform(get("/api/rewards/CUST1")
                        .param("startDate", "2026-01-01")
                        .param("endDate", "2026-01-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value("CUST1"))
                .andExpect(jsonPath("$.totalPoints").value(90));
    }

    @Test
    void shouldReturnAllRewards() throws Exception {

        RewardSummary summary = new RewardSummary(
                "CUST1",
                Map.of("Jan-2026", 90),
                90,
                List.of()
        );

        when(rewardsService.calculateAllRewards(
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 1, 31)))
                .thenReturn(List.of(summary));

        mockMvc.perform(get("/api/rewards")
                        .param("startDate", "2026-01-01")
                        .param("endDate", "2026-01-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].customerId").value("CUST1"));
    }

    @Test
    void shouldReturnBadRequestForInvalidDateRange() throws Exception {

        mockMvc.perform(get("/api/rewards/CUST1")
                        .param("startDate", "2026-02-01")
                        .param("endDate", "2026-01-01"))
                .andExpect(status().isBadRequest());
    }
}