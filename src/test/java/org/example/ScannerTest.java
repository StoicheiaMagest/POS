
package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ScannerTest {

    @Test
    void scanReturnsItemsPrice() {
        // Arrange
        Scanner scanner = new Scanner();
        Item item = mock(Item.class);
        when(item.getPrice()).thenReturn(100);

        // Act
        int result = scanner.scan(item);

        // Assert
        assertEquals(100, result);
    }

    @Test
    void scanReturnsDifferentPricesForDifferentItems() {
        // Arrange
        Scanner scanner = new Scanner();

        Item firstItem = mock(Item.class);
        when(firstItem.getPrice()).thenReturn(100);

        Item secondItem = mock(Item.class);
        when(secondItem.getPrice()).thenReturn(250);

        // Act
        int firstResult = scanner.scan(firstItem);
        int secondResult = scanner.scan(secondItem);

        // Assert
        assertEquals(100, firstResult);
        assertEquals(250, secondResult);
    }

    @Test
    void scanThrowsExceptionWhenItemIsNull() {
        // Arrange
        Scanner scanner = new Scanner();

        // Act & Assert
        assertThrows(
                NullPointerException.class,
                () -> scanner.scan(null)
        );
    }

}
