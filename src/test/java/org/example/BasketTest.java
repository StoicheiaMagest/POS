package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class BasketTest {
    private final Basket basket = new Basket();

    @Test
    void empty_basket_has_zero_price() {
        assertEquals(0, basket.getTotalPrice(), "Total price for the empty basket should be zero");
    }

    @Test
    void adding_one_item_increases_total_price() {
        // Arrange
        Item item = mock(Item.class);
        when(item.getPrice()).thenReturn(1000.0);

        // Act
        basket.addItem(item);

        // Assert
        assertEquals(1000.0, basket.getTotalPrice(), 0.001, "Total price for the basket should be 1000");
    }

}




