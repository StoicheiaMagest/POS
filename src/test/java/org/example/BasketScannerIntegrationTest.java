package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
        assertTrue(basket.getItems().contains(item));
        assertEquals(100, basket.getTotalPrice());
    }


    @Test
    void scanningTwoItemsAddsBothPricesToBasket() {
        // Arrange
        Basket basket = new Basket();
        Scanner scanner = new Scanner(basket);

        Item firstItem = mock(Item.class);
        when(firstItem.getPrice()).thenReturn(100);

        Item secondItem = mock(Item.class);
        when(secondItem.getPrice()).thenReturn(250);

        // Act
        scanner.scan(firstItem);
        scanner.scan(secondItem);

        // Assert
        assertEquals(2, basket.getItems().size());
        assertEquals(350, basket.getTotalPrice());
    }




}
