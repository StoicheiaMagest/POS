package org.example;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Tests for the price display format of spec convention C4: two decimals,
 * decimal point, space before the currency code.
 */
public class PriceFormatterTest {

    @Test
    void formatsCentsWithTwoDecimalsAndCurrency() {
        assertEquals("34.96 SEK", PriceFormatter.format(3496));
        assertEquals("0.00 SEK", PriceFormatter.format(0));
        assertEquals("1234.50 SEK", PriceFormatter.format(123450));
    }

    @Test
    void formatsNegativeCentsForDiscountLines() {
        assertEquals("-20.00 SEK", PriceFormatter.format(-2000));
    }
}
