package org.example;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Tests for BarcodeScanner: a barcode-only device owned by the purchase
 * occasion (spec &sect;4.10 / &sect;6). It resolves an article number to the
 * registry article and never touches a basket.
 */
public class BarcodeScannerTest {

    @Test
    public void scanReturnsRegisteredArticle() {
        ArticleRegistry registry = new ArticleRegistry();
        Item milk = new UnitPricedArticle("soy-milk", 3121, Vat.FOOD, 1,
                "Alpro Soymilk");
        registry.addItem(1, milk);
        BarcodeScanner scanner = new BarcodeScanner(registry);

        Item scanned = scanner.scan(1);

        assertEquals(milk, scanned);
    }

    @Test
    public void scanUnknownBarcodeThrows() {
        BarcodeScanner scanner = new BarcodeScanner(new ArticleRegistry());

        assertThrows(IllegalArgumentException.class, () -> scanner.scan(99));
    }
}
