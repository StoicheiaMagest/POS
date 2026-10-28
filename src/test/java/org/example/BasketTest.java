
package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BasketTest {

    @Test
    void newBasketHasTotalPriceZero() {
        Basket basket = new Basket();

        assertEquals(0L, basket.getTotalPrice());
    }

    @Test
    void addItemIncreasesTotalPrice() {
        Basket basket = new Basket();
        Item item = mock(Item.class);

        when(item.getInclusivePrice()).thenReturn(1000L);

        basket.addItem(item);

        assertEquals(1000L, basket.getTotalPrice());
    }

    @Test
    void addTwoItemsIncreasesTotalPriceCorrectly() {
        Basket basket = new Basket();
        Item firstItem = mock(Item.class);
        Item secondItem = mock(Item.class);

        when(firstItem.getInclusivePrice()).thenReturn(1000L);
        when(secondItem.getInclusivePrice()).thenReturn(2500L);

        basket.addItem(firstItem);
        basket.addItem(secondItem);

        assertEquals(3500L, basket.getTotalPrice());
    }

    @Test
    void addingSameItemTwiceIncreasesTotalPriceTwice() {
        Basket basket = new Basket();
        Item item = mock(Item.class);

        when(item.getInclusivePrice()).thenReturn(1000L);

        basket.addItem(item);
        basket.addItem(item);

        assertEquals(2000L, basket.getTotalPrice());
        assertEquals(2, basket.getItems().size());
    }

    @Test
    void removeItemDecreasesTotalPrice() {
        Basket basket = new Basket();
        Item item = mock(Item.class);

        when(item.getInclusivePrice()).thenReturn(1000L);

        basket.addItem(item);
        basket.removeItem(item);

        assertEquals(0L, basket.getTotalPrice());
        assertTrue(basket.getItems().isEmpty());
    }

    @Test
    void removingItemThatIsNotInBasketDoesNotChangeTotalPrice() {
        Basket basket = new Basket();
        Item itemInBasket = mock(Item.class);
        Item itemNotInBasket = mock(Item.class);

        when(itemInBasket.getInclusivePrice()).thenReturn(1000L);
        when(itemNotInBasket.getInclusivePrice()).thenReturn(500L);

        basket.addItem(itemInBasket);
        basket.removeItem(itemNotInBasket);

        assertEquals(1000L, basket.getTotalPrice());
        assertEquals(1, basket.getItems().size());
    }

    @Test
    void getItemsReturnsCopyOfItemList() {
        Basket basket = new Basket();
        Item item = mock(Item.class);

        when(item.getInclusivePrice()).thenReturn(1000L);

        basket.addItem(item);

        var items = basket.getItems();
        items.clear();

        assertEquals(1, basket.getItems().size());
    }

    @Test
    void totalPriceIsCalculatedUsingPricesIncludingVat() {
        Basket basket = new Basket();
        Item item = mock(Item.class);

        when(item.getPrice()).thenReturn(1000L);
        when(item.getInclusivePrice()).thenReturn(1250L);

        basket.addItem(item);

        assertEquals(1250L, basket.getTotalPrice());
    }

}
