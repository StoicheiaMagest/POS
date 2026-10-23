package org.example;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class MoneyTest {

    @Test
    void constructorStoresPositiveAmount() {
        Money money = new Money(10);

        assertEquals(10, money.getAmount());
    }

    @Test
    void constructorRejectsZero() {
        assertThrows(IllegalArgumentException.class, () -> new Money(0));
    }

    @Test
    void constructorRejectsNegativeAmount() {
        assertThrows(IllegalArgumentException.class, () -> new Money(-1));
    }

    @Test
    void formatUsesGivenDecimalSeparator() {
        assertEquals("11,20", Money.format(1120, ','));
        assertEquals("11.20", Money.format(1120, '.'));
    }

    @Test
    void formatZero() {
        assertEquals("0,00", Money.format(0, ','));
    }

    @Test
    void formatGroupsThousandsWithNonBreakingSpace() {
        assertEquals("1 234,56", Money.format(123456, ','));
    }

    @Test
    void formatDecimalUsesGivenNumberOfDecimals() {
        assertEquals("0,520", Money.formatDecimal(520, 3, ','));
        assertEquals("0.520", Money.formatDecimal(520, 3, '.'));
    }
}
