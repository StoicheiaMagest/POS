
package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BarcodeScannerTest {

    @Test
    void scanReturnsRegisteredItem() {
        // Arrange
        ArticleRegistry registry = mock(ArticleRegistry.class);
        BarcodeScanner scanner = new BarcodeScanner(registry);

        Item item = mock(Item.class);
        when(registry.findByArticleNumber(1001)).thenReturn(item);

        // Act
        Item result = scanner.scan(1001);

        // Assert
        assertEquals(item, result);
    }

    @Test
    void scanReturnsDifferentItemsForDifferentArticleNumbers() {
        ArticleRegistry registry = mock(ArticleRegistry.class);
        BarcodeScanner scanner = new BarcodeScanner(registry);

        Item firstItem = mock(Item.class);
        when(registry.findByArticleNumber(1001)).thenReturn(firstItem);

        Item secondItem = mock(Item.class);
        when(registry.findByArticleNumber(1002)).thenReturn(secondItem);

        Item firstResult = scanner.scan(1001);
        Item secondResult = scanner.scan(1002);

        assertEquals(firstItem, firstResult);
        assertEquals(secondItem, secondResult);
    }


    @Test
    void scanThrowsExceptionWhenArticleDoesNotExist() {
        // Arrange
        ArticleRegistry registry = mock(ArticleRegistry.class);
        BarcodeScanner scanner = new BarcodeScanner(registry);

        when(registry.findByArticleNumber(9999)).thenReturn(null);

        // Act & Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> scanner.scan(9999)
        );
    }
}
