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
        BarcodeScanner scanner = new BarcodeScanner(basket, registry);

        Item item = mock(Item.class);
        when(item.getPrice()).thenReturn(100L);
        when(item.getInclusivePrice()).thenReturn(100L);

        registry.registerItem(1001, item);

        long result = scanner.scan(1001);

        assertEquals(100L, result);
        assertTrue(basket.getItems().contains(item));
        assertEquals(100L, basket.getTotalPrice());
    }

    @Test
    void scanningTwoItemsAddsBothPricesToBasket() {
        Basket basket = new Basket();
        ArticleRegistry registry = new ArticleRegistry();
        BarcodeScanner scanner = new BarcodeScanner(basket, registry);

        Item firstItem = mock(Item.class);
        when(firstItem.getPrice()).thenReturn(100L);
        when(firstItem.getInclusivePrice()).thenReturn(100L);

        Item secondItem = mock(Item.class);
        when(secondItem.getPrice()).thenReturn(250L);
        when(secondItem.getInclusivePrice()).thenReturn(250L);

        registry.registerItem(1001, firstItem);
        registry.registerItem(1002, secondItem);

        scanner.scan(1001);
        scanner.scan(1002);

        assertEquals(2, basket.getItems().size());
        assertEquals(350L, basket.getTotalPrice());
    }

    @Test
    void scanningUnknownArticleThrowsException() {
        Basket basket = new Basket();
        ArticleRegistry registry = new ArticleRegistry();
        BarcodeScanner scanner = new BarcodeScanner(basket, registry);

        assertThrows(
                IllegalArgumentException.class,
                () -> scanner.scan(9999)
        );
    }
}
