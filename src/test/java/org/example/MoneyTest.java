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
}
