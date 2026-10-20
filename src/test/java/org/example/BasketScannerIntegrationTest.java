
package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BasketScannerIntegrationTest {

    @Test
    void scanningItemAddsItToBasket() {
        // Arrange
        Basket basket = new Basket();
        ArticleRegistry registry = new ArticleRegistry();
        Scanner scanner = new Scanner(basket, registry);

        Item item = mock(Item.class);
        when(item.getPrice()).thenReturn(100);

        registry.addItem(1001, item);

        // Act
        int result = scanner.scan(1001);

        // Assert
        assertEquals(100, result);
        assertTrue(basket.getItems().contains(item));
        assertEquals(100, basket.getTotalPrice());
    }

    @Test
    void scanningTwoItemsAddsBothPricesToBasket() {
        // Arrange
        Basket basket = new Basket();
        ArticleRegistry registry = new ArticleRegistry();
        Scanner scanner = new Scanner(basket, registry);

        Item firstItem = mock(Item.class);
        when(firstItem.getPrice()).thenReturn(100);

        Item secondItem = mock(Item.class);
        when(secondItem.getPrice()).thenReturn(250);

        registry.addItem(1001, firstItem);
        registry.addItem(1002, secondItem);

        // Act
        scanner.scan(1001);
        scanner.scan(1002);

        // Assert
        assertEquals(2, basket.getItems().size());
        assertEquals(350, basket.getTotalPrice());
    }

    @Test
    void scanningUnknownArticleThrowsException() {
        // Arrange
        Basket basket = new Basket();
        ArticleRegistry registry = new ArticleRegistry();
        Scanner scanner = new Scanner(basket, registry);

        // Act & Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> scanner.scan(9999)
        );

        // Verify that the basket remains unchanged
        assertTrue(basket.getItems().isEmpty());
        assertEquals(0, basket.getTotalPrice());
    }
}
