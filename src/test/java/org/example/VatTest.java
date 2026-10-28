package org.example;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
}
