
package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class VatRateTest {

    @Test
    void standard_vat_rate_should_be_25_percent() {
        assertEquals(0.25, VatRate.STANDARD.getRate(), 0.000001);
    }

    @Test
    void food_vat_rate_should_be_12_percent() {
        assertEquals(0.12, VatRate.FOOD_12.getRate(), 0.000001);
    }

    @Test
    void book_vat_rate_should_be_6_percent() {
        assertEquals(0.06, VatRate.BOOK_6.getRate(), 0.000001);
    }
}
