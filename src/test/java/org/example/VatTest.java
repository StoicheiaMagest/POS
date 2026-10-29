package org.example;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class VatTest {

    private long priceInCentsBeforeVat;
    private static final long STANDARD = 2500;
    private static final long FOOD = 1200;
    private static final long BOOK = 600;
    private Vat vat;

    @ParameterizedTest(name = "{3} [{1}: {0} cents -> {2}]")
    @CsvSource({
            "1000, STANDARD, 250, exact amount needs no rounding",
            "149,  STANDARD, 37,  rounds down below half",
            "150,  STANDARD, 38,  rounds up at exactly half",
            "2,    STANDARD, 1,   rounds up at exactly half on a tiny price",
            "1,    STANDARD, 0,   rounds down to zero",
            "0,    STANDARD, 0,   zero price has zero VAT",
            "999,  FOOD,     120, rounds up above half",
            "25,   BOOK,     2,   rounds up at exactly half",
            "8,    BOOK,     0,   rounds down below half"
    })
    void calculateVatAmount_rounds_to_nearest_cent(long priceInCents, Vat vat, long expected, String description) {
        assertEquals(expected, vat.calculateVatAmount(priceInCents));
    }

    @ParameterizedTest(name = "{3} [{1}: {0} cents -> {2}]")
    @CsvSource({
            "1000, STANDARD, 1250, exact amount adds whole VAT",
            "149,  STANDARD, 186, VAT rounded down is added",
            "150,  STANDARD, 188,  VAT rounded up is added",
            "999,  FOOD, 1119, food rate",
            "25,   BOOK, 27, 999 , book rate",
            "0,    STANDARD, 0, zero price stays zero (standard rate)",
            "0,    FOOD, 0, zero price stays zero (food rate)",
            "0,    BOOK, 0, zero price stays zero (book rate)"
    })
    void calculateInclusivePrice_adds_vat_to_price(long priceInCents, Vat vat, long expected, String description) {
        assertEquals(expected, vat.calculateInclusivePrice(priceInCents));
    }

    @ParameterizedTest(name = "{3} [{1}: {0} cents -> {2}]")
    @CsvSource({
            "1250, STANDARD, 1000, exact amount needs no rounding",
            "188,  STANDARD, 150,  150.40 rounds down",
            "189,  STANDARD, 151,  151.20 rounds down",
            "1000, FOOD,     893,  892.86 rounds up",
            "106,  BOOK,     100,  exact amount for book rate",
            "14,   FOOD,     13,   12.50 rounds up at exactly half",
            "0,    STANDARD, 0,    zero price stays zero (standard rate)",
            "0,    FOOD,     0,    zero price stays zero (food rate)",
            "0,    BOOK,     0,    zero price stays zero (book rate)"
    })
    void calculateExclusivePrice_removes_vat_from_price(long priceWithVat, Vat vat, long expected, String description) {
        assertEquals(expected, vat.calculateExclusivePrice(priceWithVat));
    }

    @ParameterizedTest(name = "{3} [{1}: {0} cents -> {2}]")
    @CsvSource({
            "1, STANDARD, 0, last price with no VAT (0.25)",
            "2, STANDARD, 1, first price with VAT (0.50)",
            "4, FOOD,     0, last price with no VAT (0.48)",
            "5, FOOD,     1, first price with VAT (0.60)",
            "8, BOOK,     0, last price with no VAT (0.48)",
            "9, BOOK,     1, first price with VAT (0.54)"
    })
    void calculateVatAmount_rounding_flips_at_boundary_price(long priceInCents, Vat vat, long expected, String description) {
        assertEquals(expected, vat.calculateVatAmount(priceInCents));
    }

    @ParameterizedTest(name = "{2} [{1}: {0} cents]")
    @CsvSource({
            "-1,                    STANDARD, smallest negative price",
            "-1,                    FOOD,     smallest negative price (food rate)",
            "-1,                    BOOK,     smallest negative price (book rate)",
            "-100,                  STANDARD, ordinary negative price",
            "-9223372036854775808,  STANDARD, Long.MIN_VALUE"
    })
    void calculateVatAmount_rejects_negative_price(long priceInCents, Vat vat, String description) {
        assertThrows(IllegalArgumentException.class, () -> vat.calculateVatAmount(priceInCents));
    }

    @ParameterizedTest(name = "{2} [{1}: {0} cents]")
    @CsvSource({
            "-1,                    STANDARD, smallest negative price",
            "-1,                    FOOD,     smallest negative price (food rate)",
            "-1,                    BOOK,     smallest negative price (book rate)",
            "-100,                  STANDARD, ordinary negative price",
            "-9223372036854775808,  STANDARD, Long.MIN_VALUE"
    })
    void calculateInclusivePrice_rejects_negative_price(long priceInCents, Vat vat, String description) {
        assertThrows(IllegalArgumentException.class, () -> vat.calculateInclusivePrice(priceInCents));
    }

    @ParameterizedTest(name = "{2} [{1}: {0} cents]")
    @CsvSource({
            "-1,                    STANDARD, smallest negative price",
            "-1,                    FOOD,     smallest negative price (food rate)",
            "-1,                    BOOK,     smallest negative price (book rate)",
            "-100,                  STANDARD, ordinary negative price",
            "-9223372036854775808,  STANDARD, Long.MIN_VALUE"
    })
    void calculateExclusivePrice_rejects_negative_price(long priceWithVat, Vat vat, String description) {
        assertThrows(IllegalArgumentException.class, () -> vat.calculateExclusivePrice(priceWithVat));
    }

}
