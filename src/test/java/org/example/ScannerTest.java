
package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ScannerTest {

    @Test
    void scanReturnsPriceOfRegisteredItem() {
        // Arrange
        Basket basket = new Basket();
        ArticleRegistry registry = mock(ArticleRegistry.class);
        Scanner scanner = new Scanner(basket, registry);

        Item item = mock(Item.class);
        when(item.getPrice()).thenReturn(100l);
        when(registry.findByArticleNumber(1001)).thenReturn(item);

        // Act
        long result = scanner.scan(1001);

        // Assert
        assertEquals(100, result);
    }

    @Test
    void scanReturnsDifferentPricesForDifferentItems() {
        // Arrange
        Basket basket = new Basket();
        ArticleRegistry registry = mock(ArticleRegistry.class);
        Scanner scanner = new Scanner(basket, registry);

        Item firstItem = mock(Item.class);
        when(firstItem.getPrice()).thenReturn(100l);
        when(registry.findByArticleNumber(1001)).thenReturn(firstItem);

        Item secondItem = mock(Item.class);
        when(secondItem.getPrice()).thenReturn(250l);
        when(registry.findByArticleNumber(1002)).thenReturn(secondItem);

        // Act
        long firstResult = scanner.scan(100);
        long secondResult = scanner.scan(1002);

        // Assert
        assertEquals(100, firstResult);
        assertEquals(250, secondResult);
    }

    @Test
    void scanThrowsExceptionWhenArticleDoesNotExist() {
        // Arrange
        Basket basket = new Basket();
        ArticleRegistry registry = mock(ArticleRegistry.class);
        Scanner scanner = new Scanner(basket, registry);

        when(registry.findByArticleNumber(9999)).thenReturn(null);

        // Act & Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> scanner.scan(9999)
        );
    }
}
