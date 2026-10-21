package org.example;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
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

    @Test
    void removing_one_item_keeps_the_other_item_in_total() {
        // Arrange
        Item firstItem = mock(Item.class);
        Item secondItem = mock(Item.class);

        when(firstItem.getPrice()).thenReturn(1000);
        when(secondItem.getPrice()).thenReturn(500);

        basket.addItem(firstItem);
        basket.addItem(secondItem);

        // Act
        basket.removeItem(firstItem);

        // Assert
        assertEquals(500.0, basket.getTotalPrice(), 0.001,
                "The total price should include only the remaining item");
    }

    @Test
    void basket_can_be_used_again_after_being_emptied() {
        // Arrange
        Item firstItem = mock(Item.class);
        Item secondItem = mock(Item.class);

        when(firstItem.getPrice()).thenReturn(1000);
        when(secondItem.getPrice()).thenReturn(500);

        basket.addItem(firstItem);
        basket.removeItem(firstItem);

        // Act
        basket.addItem(secondItem);

        // Assert
        assertEquals(500.0, basket.getTotalPrice(), 0.001,
                "The basket should work correctly after being emptied");
    }

    @Test
    void removeOne_removes_exactly_one_unit_entry_by_handle() {
        // Arrange
        Basket basket = new Basket();
        basket.addItem(soyMilk());
        basket.addItem(soyMilk());

        // Act
        Item removed = basket.removeOne("soy-milk");

        // Assert
        assertNotNull(removed);
        assertEquals(1, basket.getItems().size(),
                "exactly one piece should have been removed");
    }

    @Test
    void removeOne_returns_null_for_absent_item() {
        Basket basket = new Basket();

        assertNull(basket.removeOne("soy-milk"));
    }

    @Test
    void lines_group_unit_articles_and_keep_weighed_entries_separate() {
        // Arrange
        Basket basket = new Basket();
        basket.addItem(soyMilk());
        basket.addItem(soyMilk());
        basket.addItem(soyMilk());
        basket.addItem(new WeighedItem("apple", 2223, Vat.FOOD, 300, 9,
                "Apple"));
        basket.addItem(new WeighedItem("apple", 2223, Vat.FOOD, 200, 9,
                "Apple"));

        // Act
        List<BasketLine> lines = basket.lines();

        // Assert
        assertEquals(3, lines.size());
        assertEquals("3", lines.get(0).getQuantity());
        assertEquals("Alpro Soymilk", lines.get(0).getDescription());
        assertEquals("300g", lines.get(1).getQuantity());
        assertEquals("Apple", lines.get(1).getDescription());
        assertEquals("200g", lines.get(2).getQuantity());
    }

    private static Item soyMilk() {
        return new UnitPricedArticle("soy-milk", 3121, Vat.FOOD, 1,
                "Alpro Soymilk");
    }

}




