package org.example;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

// The getters on PurchaseOccasion that a Receipt reads its data from.
public class PurchaseOccasionReceiptDataTest {

    private Store createStore() {
        ArticleRegistry registry = new ArticleRegistry();
        BarcodeScanner scanner = new BarcodeScanner(registry);
        return new Store(registry, new Account(), new DiscountHandler(), scanner);
    }

    // getItems

    @Test
    void getItemsReturnsTheItemsFromTheBasket() {
        Basket basket = new Basket();
        Item milk = mock(Item.class);
        Item bread = mock(Item.class);
        basket.addItem(milk);
        basket.addItem(bread);
        PurchaseOccasion occasion =
                new PurchaseOccasion(basket, mock(Customer.class), mock(Store.class));

        assertEquals(List.of(milk, bread), occasion.getItems());
    }

    @Test
    void changingTheReturnedListDoesNotChangeTheOccasion() {
        Basket basket = new Basket();
        basket.addItem(mock(Item.class));
        PurchaseOccasion occasion =
                new PurchaseOccasion(basket, mock(Customer.class), mock(Store.class));

        occasion.getItems().clear();

        assertEquals(1, occasion.getItems().size());
    }

    // getDiscounts

    @Test
    void getDiscountsIsEmptyBeforeDiscountsAreApplied() {
        PurchaseOccasion occasion = new PurchaseOccasion(
                new Basket(), mock(Customer.class), createStore());

        assertTrue(occasion.getDiscounts().isEmpty());
    }

    @Test
    void getDiscountsReturnsTheAppliedMultiItemDiscounts() {
        Item milk = mock(Item.class);
        when(milk.getArticleNumber()).thenReturn(1001);
        when(milk.getName()).thenReturn("Mjölk");
        when(milk.getPrice()).thenReturn(100L);
        when(milk.getInclusivePrice()).thenReturn(100L);
        when(milk.getDiscountType()).thenReturn("3FOR2");
        Basket basket = new Basket();
        basket.addItem(milk);
        basket.addItem(milk);
        basket.addItem(milk);
        Customer customer = new Customer("Stig", "19900101-1234", false, false);
        PurchaseOccasion occasion = new PurchaseOccasion(basket, customer, createStore());

        occasion.applyDiscounts();

        assertEquals(Map.of("3FOR2 on Mjölk", 100L), occasion.getDiscounts());
    }

    @Test
    void getDiscountsReturnsAMapThatCannotBeChanged() {
        PurchaseOccasion occasion = new PurchaseOccasion(
                new Basket(), mock(Customer.class), createStore());

        assertThrows(UnsupportedOperationException.class,
                () -> occasion.getDiscounts().put("fake", 1L));
    }
}
