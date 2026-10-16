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
        when(item.getPrice()).thenReturn(1000);

        // Act
        basket.addItem(item);

        // Assert
        assertEquals(1000.0, basket.getTotalPrice(), 0.001, "Total price for the basket should be 1000");
    }

    @Test
    void adding_two_items_calculates_total_price() {
        // Arrange
        Item firstItem = mock(Item.class);
        Item secondItem = mock(Item.class);

        when(firstItem.getPrice()).thenReturn(1000);
        when(secondItem.getPrice()).thenReturn(2500);

        // Act
        basket.addItem(firstItem);
        basket.addItem(secondItem);

        // Assert
        assertEquals(
                3500.0,
                basket.getTotalPrice(),
                0.001,
                "Total price should be the sum of both items"
        );
    }

    @Test
    void adding_same_item_twice_counts_its_price_twice() {
        // Arrange
        Item item = mock(Item.class);
        when(item.getPrice()).thenReturn(1000);

        // Act
        basket.addItem(item);
        basket.addItem(item);

        // Assert
        assertEquals(
                2000.0,
                basket.getTotalPrice(),
                0.001,
                "Adding the same item twice should count its price twice"
        );
    }

    @Test
    void removing_item_decreases_total_price() {
        // Arrange
        Item item = mock(Item.class);
        when(item.getPrice()).thenReturn(1000);

        basket.addItem(item);

        // Act
        basket.removeItem(item);

        // Assert
        assertEquals(
                0.0,
                basket.getTotalPrice(),
                0.001,
                "Removing the only item should return the total price to zero"
        );
    }

    @Test
    void removing_item_not_in_basket_does_not_change_total_price() {
        // Arrange
        Item existingItem = mock(Item.class);
        Item absentItem = mock(Item.class);

        when(existingItem.getPrice()).thenReturn(1000);
        when(absentItem.getPrice()).thenReturn(500);

        basket.addItem(existingItem);

        // Act
        basket.removeItem(absentItem);

        // Assert
        assertEquals(
                1000.0,
                basket.getTotalPrice(),
                0.001,
                "Removing an absent item should not change the total price"
        );
    }

}




