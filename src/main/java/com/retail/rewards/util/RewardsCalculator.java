package com.retail.rewards.util;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class RewardsCalculator {

    private static final BigDecimal FIFTY_DOLLARS = BigDecimal.valueOf(50);
    private static final BigDecimal HUNDRED_DOLLARS = BigDecimal.valueOf(100);
    private static final BigDecimal TWO = BigDecimal.valueOf(2);

    public int calculatePoints(BigDecimal amount) {

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return 0;
        }

        // Ignore cents while calculating reward points
        BigDecimal roundedAmount = amount.setScale(0, RoundingMode.DOWN);

        int points = 0;

        if (roundedAmount.compareTo(HUNDRED_DOLLARS) > 0) {

            points += roundedAmount
                    .subtract(HUNDRED_DOLLARS)
                    .multiply(TWO)
                    .intValue();

            points += 50;

        } else if (roundedAmount.compareTo(FIFTY_DOLLARS) > 0) {

            points += roundedAmount
                    .subtract(FIFTY_DOLLARS)
                    .intValue();
        }

        return points;
    }
}