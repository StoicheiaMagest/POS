
package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BasketScannerIntegrationTest {

    @Test
    void scanningItemAddsItToBasket() {
        Basket basket = new Basket();
        ArticleRegistry registry = new ArticleRegistry();
        BarcodeScanner scanner = new BarcodeScanner(registry);

        Item item = mock(Item.class);
        when(item.getInclusivePrice()).thenReturn(100L);

        registry.addItem(1001, item);

        basket.addItem(scanner.scan(1001));

        assertTrue(basket.getItems().contains(item));
        assertEquals(100L, basket.getTotalPrice());
    }

    @Test
    void scanningTwoItemsAddsBothPricesToBasket() {
        Basket basket = new Basket();
        ArticleRegistry registry = new ArticleRegistry();
        BarcodeScanner scanner = new BarcodeScanner(registry);

        Item firstItem = mock(Item.class);
        when(firstItem.getInclusivePrice()).thenReturn(100L);

        Item secondItem = mock(Item.class);
        when(secondItem.getInclusivePrice()).thenReturn(250L);

        registry.addItem(1001, firstItem);
        registry.addItem(1002, secondItem);

        basket.addItem(scanner.scan(1001));
        basket.addItem(scanner.scan(1002));

        assertEquals(2, basket.getItems().size());
        assertEquals(350L, basket.getTotalPrice());
    }

    @Test
    void scanningUnknownArticleThrowsException() {
        Basket basket = new Basket();
        ArticleRegistry registry = new ArticleRegistry();
        BarcodeScanner scanner = new BarcodeScanner(registry);

        assertThrows(
                IllegalArgumentException.class,
                () -> scanner.scan(9999)
        );
    }
}
