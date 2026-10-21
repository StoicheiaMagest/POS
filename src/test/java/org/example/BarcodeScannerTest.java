package org.example;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

/**
 * Tests for BarcodeScanner: a barcode-only device owned by the purchase
 * occasion (spec &sect;4.10 / &sect;6). A barcode is an integer carrying the
 * article id and, for weighed goods, the weight in its last digits — like a
 * scale printout label. The scanner resolves it to the sale item and never
 * touches a basket.
 */
public class BarcodeScannerTest {

    @Test
    void scanReturnsRegisteredUnitArticle() {
        ArticleRegistry registry = new ArticleRegistry();
        Item milk = new UnitPricedArticle("soy-milk", 3121, Vat.FOOD, 1,
                "Alpro Soymilk");
        registry.addItem(1, milk);
        BarcodeScanner scanner = new BarcodeScanner(registry);

        Item scanned = scanner.scan(1);

        assertSame(milk, scanned);
    }

    @Test
    void scanUnknownBarcodeThrows() {
        BarcodeScanner scanner = new BarcodeScanner(new ArticleRegistry());

        assertThrows(IllegalArgumentException.class, () -> scanner.scan(99));
    }

    @Test
    void weighedBarcodeResolvesToArticleWithEncodedWeight() {
        // Arrange: apple (id 9) with 300 g encoded in the last digits
        ArticleRegistry registry = ArticleRegistry.withStandardAssortment();
        BarcodeScanner scanner = new BarcodeScanner(registry);

        Item scanned = scanner.scan(9 * 100000 + 300);

        assertEquals("apple", scanned.getName());
        assertEquals(300, ((WeighedItem) scanned).getWeight());
        // 2223 cents/kg base * 300 g / 1000 = 666 cents
        assertEquals(666, scanned.getPrice());
    }

    @Test
    void weighedBarcodeWithUnknownArticleThrows() {
        BarcodeScanner scanner = new BarcodeScanner(new ArticleRegistry());

        assertThrows(IllegalArgumentException.class,
                () -> scanner.scan(98 * 100000 + 300));
    }

    @Test
    void barcodeOfRoundTripsThroughScan() {
        ArticleRegistry registry = ArticleRegistry.withStandardAssortment();
        BarcodeScanner scanner = new BarcodeScanner(registry);
        Item apple300 = new WeighedItem("apple", 2223, Vat.FOOD, 300, 9,
                "Apple");
        Item milk = registry.findByName("soy-milk");

        assertEquals(300, ((WeighedItem) scanner.scan(
                BarcodeScanner.barcodeOf(apple300))).getWeight());
        assertSame(milk, scanner.scan(BarcodeScanner.barcodeOf(milk)));
    }
}
