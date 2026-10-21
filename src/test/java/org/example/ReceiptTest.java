package org.example;

import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for the single receipt renderer of spec &sect;4.15: store name, one
 * line per article with quantity/weight and price, an optional discount
 * line and the total paid.
 */
public class ReceiptTest {

    @Test
    void listsArticlesWithQuantityAndPriceAndTotalPaid() {
        List<ScanLine> lines = List.of(
                new ScanLine("1", "Alpro Soymilk", 3496, 3496),
                new ScanLine("2", "Tropicana Orange Juice", 5990, 9486));

        String receipt = new Receipt("Kwik E-Smart", lines, 0, 9486).print();

        assertTrue(receipt.contains("Kwik E-Smart"), receipt);
        assertTrue(receipt.contains("1"), receipt);
        assertTrue(receipt.contains("Alpro Soymilk"), receipt);
        assertTrue(receipt.contains("34.96 SEK"), receipt);
        assertTrue(receipt.contains("2"), receipt);
        assertTrue(receipt.contains("Tropicana Orange Juice"), receipt);
        assertTrue(receipt.contains("59.90 SEK"), receipt);
        assertFalse(receipt.contains("Discount"), receipt);
        assertTrue(receipt.contains("Total paid: 94.86 SEK"), receipt);
        assertTrue(receipt.endsWith("Thank you for shopping at Kwik E-Smart!"),
                receipt);
    }

    @Test
    void showsDiscountLineWhenDiscountWasApplied() {
        List<ScanLine> lines = List.of(
                new ScanLine("1", "Alpro Soymilk", 3496, 3496));

        String receipt =
                new Receipt("Kwik E-Smart", lines, 700, 2796).print();

        assertTrue(receipt.contains("Discount -20%: -7.00 SEK"), receipt);
        assertTrue(receipt.contains("Total paid: 27.96 SEK"), receipt);
    }
}
