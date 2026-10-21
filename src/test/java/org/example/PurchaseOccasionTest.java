package org.example;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PurchaseOccasionTest {

    @Test
    void scanAllGroupsArticlesAndReportsRunningInclusiveTotals() {
        // Arrange: assortment from spec §5 (soy-milk 34.96, juice 2×29.95,
        // apple 300 g at 24.90/kg inclusive)
        ArticleRegistry registry = ArticleRegistry.withStandardAssortment();
        Basket basket = new Basket();
        basket.addItem(registry.findByName("soy-milk"));
        basket.addItem(registry.findByName("orange-juice"));
        basket.addItem(registry.findByName("orange-juice"));
        basket.addItem(new WeighedItem("apple", 2223, Vat.FOOD, 300, 9,
                "Apple"));
        PurchaseOccasion purchase = new PurchaseOccasion(
                new BarcodeScanner(registry), basket,
                new Customer("Test Customer", "19900101-1234"),
                new Account(0));

        // Act
        List<ScanLine> lines = purchase.scanAll();

        // Assert: grouped scan lines with cumulative inclusive totals
        assertEquals(3, lines.size());
        assertEquals("1", lines.get(0).getQuantity());
        assertEquals("Alpro Soymilk", lines.get(0).getDescription());
        assertEquals(3496, lines.get(0).getRunningTotalCents());
        assertEquals("2", lines.get(1).getQuantity());
        assertEquals(3496 + 5990, lines.get(1).getRunningTotalCents());
        assertEquals("300g", lines.get(2).getQuantity());
        assertEquals("Apple", lines.get(2).getDescription());
        long appleLine = new WeighedItem("apple", 2223, Vat.FOOD, 300, 9,
                "Apple").getInclusivePrice();
        assertEquals(3496 + 5990 + appleLine,
                lines.get(2).getRunningTotalCents());
    }

    @Test
    void scanningTwiceIsRefused() {
        ArticleRegistry registry = ArticleRegistry.withStandardAssortment();
        Basket basket = new Basket();
        basket.addItem(registry.findByName("soy-milk"));
        PurchaseOccasion purchase = new PurchaseOccasion(
                new BarcodeScanner(registry), basket,
                new Customer("Test Customer", "19900101-1234"),
                new Account(0));
        purchase.scanAll();

        assertTrue(purchase.isScanned());
        assertThrows(IllegalStateException.class, purchase::scanAll);
    }
}
