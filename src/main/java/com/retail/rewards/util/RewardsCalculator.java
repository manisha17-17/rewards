package com.retail.rewards.util;

import org.springframework.stereotype.Component;

@Component
public class RewardsCalculator {

    private static final double MIN_THRESHOLD = 50.0;
    private static final double MAX_THRESHOLD = 100.0;
    private static final int POINTS_PER_DOLLAR_LOW = 1;
    private static final int POINTS_PER_DOLLAR_HIGH = 2;

    public int calculatePoints(double amount) {
        int points = 0;

        if (amount > MAX_THRESHOLD) {
            points += (int) (amount - MAX_THRESHOLD) * POINTS_PER_DOLLAR_HIGH;
            points += (int) (MAX_THRESHOLD - MIN_THRESHOLD) * POINTS_PER_DOLLAR_LOW;
        } else if (amount > MIN_THRESHOLD) {
            points += (int) (amount - MIN_THRESHOLD) * POINTS_PER_DOLLAR_LOW;
        }

        return points;
    }
}
