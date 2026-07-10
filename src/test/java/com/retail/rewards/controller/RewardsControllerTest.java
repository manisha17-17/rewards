package com.retail.rewards.controller;

import com.retail.rewards.model.RewardSummary;
import com.retail.rewards.service.RewardsService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(RewardsController.class)
public class RewardsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RewardsService service;

    @Test
    public void testGetRewardsForSingleCustomer_Success() throws Exception {

        String customerId = "CUST1";
        Map<Month, Integer> monthlyPoints = new HashMap<>();
        monthlyPoints.put(Month.JANUARY, 90);
        RewardSummary mockSummary = new RewardSummary(customerId, monthlyPoints, 90, Collections.emptyList());

        Mockito.when(service.calculateRewards(eq(customerId), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(mockSummary);

        // Act & Assert
        mockMvc.perform(get("/api/rewards/" + customerId)
                        .param("startDate", "2026-01-01")
                        .param("endDate", "2026-01-31")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value("CUST1"))
                .andExpect(jsonPath("$.totalPoints").value(90))
                .andExpect(jsonPath("$.monthlyPoints.JANUARY").value(90));
    }

    @Test
    public void testGetAllRewards_Success() throws Exception {
        // Arrange
        Map<Month, Integer> points1 = new HashMap<>();
        points1.put(Month.JANUARY, 90);
        RewardSummary summary1 = new RewardSummary("CUST1", points1, 90, Collections.emptyList());

        Map<Month, Integer> points2 = new HashMap<>();
        points2.put(Month.JANUARY, 45);
        RewardSummary summary2 = new RewardSummary("CUST2", points2, 45, Collections.emptyList());

        Mockito.when(service.calculateAllRewards(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(Arrays.asList(summary1, summary2));


        mockMvc.perform(get("/api/rewards/allCustomers")
                        .param("startDate", "2026-01-01")
                        .param("endDate", "2026-01-31")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].customerId").value("CUST1"))
                .andExpect(jsonPath("$[0].totalPoints").value(90))
                .andExpect(jsonPath("$[1].customerId").value("CUST2"))
                .andExpect(jsonPath("$[1].totalPoints").value(45));
    }

    @Test
    public void testGetRewards_MissingParameters_ReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/rewards/allCustomers")
                        .param("startDate", "2026-01-01")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
