
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
        Scanner scanner = new Scanner(basket, registry);

        Item item = mock(Item.class);
        when(item.getPrice()).thenReturn(100);
        when(item.getInclusivePrice()).thenReturn(100L);

        registry.addItem(1001, item);

        int result = scanner.scan(1001);

        assertEquals(100, result);
        assertTrue(basket.getItems().contains(item));
        assertEquals(100L, basket.getTotalPrice());
    }

    @Test
    void scanningTwoItemsAddsBothPricesToBasket() {
        Basket basket = new Basket();
        ArticleRegistry registry = new ArticleRegistry();
        Scanner scanner = new Scanner(basket, registry);

        Item firstItem = mock(Item.class);
        when(firstItem.getPrice()).thenReturn(100);
        when(firstItem.getInclusivePrice()).thenReturn(100L);

        Item secondItem = mock(Item.class);
        when(secondItem.getPrice()).thenReturn(250);
        when(secondItem.getInclusivePrice()).thenReturn(250L);

        registry.addItem(1001, firstItem);
        registry.addItem(1002, secondItem);

        scanner.scan(1001);
        scanner.scan(1002);

        assertEquals(2, basket.getItems().size());
        assertEquals(350L, basket.getTotalPrice());
    }

    @Test
    void scanningUnknownArticleThrowsException() {
        Basket basket = new Basket();
        ArticleRegistry registry = new ArticleRegistry();
        Scanner scanner = new Scanner(basket, registry);

        assertThrows(
                IllegalArgumentException.class,
                () -> scanner.scan(9999)
        );
    }
}
