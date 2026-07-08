package com.retail.rewards.util;



import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RewardsCalculatorTest {

    private final RewardsCalculator calculator = new RewardsCalculator();

    @ParameterizedTest(name = "Purchase amount {0} should earn {1} points")
    @CsvSource({
            "45.00,  0",    // Below low threshold
            "50.00,  0",    // Exactly at low threshold edge
            "75.50,  25",   // In the middle of low threshold ($75.50 - $50 = $25.50 -> 25 pts due to truncation)
            "100.00, 50",   // Exactly at high threshold edge ($100 - $50 = 50 pts)
            "120.00, 90"    // Over high threshold ($20 * 2 + $50 * 1 = 90 pts)
    })
    @DisplayName("Should calculate points correctly for varied standard transaction scenarios")
    void calculatePoints_StandardThresholdBoundaries(double amount, int expectedPoints) {
        int actualPoints = calculator.calculatePoints(amount);
        assertEquals(expectedPoints, actualPoints, "Points miscalculation for amount: " + amount);
    }

    @Test
    @DisplayName("Should demonstrate the current integer truncation behavior for double parameters over $100")
    void calculatePoints_DemonstrateTruncationBehavior() {
        // Current code does: (int)(120.50 - 100) * 2 -> 20 * 2 = 40 + 50 = 90
        int points = calculator.calculatePoints(120.50);
        assertEquals(90, points, "Should match the current code's pre-multiplication casting outcome");
    }
}

