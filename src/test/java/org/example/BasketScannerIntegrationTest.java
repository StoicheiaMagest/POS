package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
public class BasketScannerIntegrationTest {


    @Test
    void scanningItemAddsItToBasket() {
        // Arrange
        Basket basket = new Basket();
        Scanner scanner = new Scanner(basket);
        Item item = mock(Item.class);

        when(item.getPrice()).thenReturn(100);

        // Act
        scanner.scan(item);

        // Assert
        assertEquals(100, basket.getTotalPrice());
    }

}
