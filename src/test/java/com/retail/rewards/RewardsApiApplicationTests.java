package com.retail.rewards;

import com.retail.rewards.util.RewardsCalculator;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RewardsCalculatorTest {

	private final RewardsCalculator calculator = new RewardsCalculator();

	@Test
	void shouldReturnZeroForNullAmount() {
		assertEquals(0, calculator.calculatePoints(null));
	}

	@Test
	void shouldReturnZeroForZeroAmount() {
		assertEquals(0,
				calculator.calculatePoints(BigDecimal.ZERO));
	}

	@Test
	void shouldReturnZeroForAmountLessThan50() {
		assertEquals(0,
				calculator.calculatePoints(new BigDecimal("49")));
	}

	@Test
	void shouldReturnZeroForAmountExactly50() {
		assertEquals(0,
				calculator.calculatePoints(new BigDecimal("50")));
	}

	@Test
	void shouldReturnOnePointFor51() {
		assertEquals(1,
				calculator.calculatePoints(new BigDecimal("51")));
	}

	@Test
	void shouldReturnFortyNinePointsFor99() {
		assertEquals(49,
				calculator.calculatePoints(new BigDecimal("99")));
	}

	@Test
	void shouldReturnFiftyPointsFor100() {
		assertEquals(50,
				calculator.calculatePoints(new BigDecimal("100")));
	}

	@Test
	void shouldReturnFiftyTwoPointsFor101() {
		assertEquals(52,
				calculator.calculatePoints(new BigDecimal("101")));
	}

	@Test
	void shouldReturnNinetyPointsFor120() {
		assertEquals(90,
				calculator.calculatePoints(new BigDecimal("120")));
	}

	@Test
	void shouldRoundDownDecimalAmount() {
		assertEquals(90,
				calculator.calculatePoints(new BigDecimal("120.99")));
	}

	@Test
	void shouldReturnOneHundredTwentyPointsFor135() {
		assertEquals(120,
				calculator.calculatePoints(new BigDecimal("135")));
	}

	@Test
	void shouldReturnOneHundredFiftyPointsFor150() {
		assertEquals(150,
				calculator.calculatePoints(new BigDecimal("150")));
	}
}