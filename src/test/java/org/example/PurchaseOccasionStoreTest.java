package org.example;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

import java.util.List;

public class PurchaseOccasionStoreTest {

    private Store createStore() {
        ArticleRegistry registry = new ArticleRegistry();
        BarcodeScanner scanner = new BarcodeScanner(registry);
        return new Store(registry, new Account(), new DiscountHandler(), scanner);
    }

    private Item itemWithPrice(long priceInOre) {
        Item item = mock(Item.class);
        when(item.getPrice()).thenReturn(priceInOre);
        when(item.getInclusivePrice()).thenReturn(priceInOre);
        return item;
    }

    @Test
    void purchaseOccasionIsBuiltFromBasketCustomerAndStore() {
        Basket basket = new Basket();
        basket.addItem(itemWithPrice(1000));
        Customer customer = new Customer("Alex", "19900101-1234", false, false);
        Store store = createStore();

        PurchaseOccasion purchase = new PurchaseOccasion(basket, customer, store);

        assertEquals(1000L, purchase.getTotalPriceForPurchase());
    }

    // applying discounts twice would subtract every discount a second time,
    // so the second call must be refused
    @Test
    void applyDiscountsTwiceFails() {
        Store store = createStore();
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
        PurchaseOccasion purchase = new PurchaseOccasion(basket, customer, store);

        purchase.applyDiscounts();

        assertThrows(IllegalStateException.class, () -> purchase.applyDiscounts());
        // the total is untouched by the refused second call
        assertEquals(200L, purchase.getTotalPriceForPurchase());
    }

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
}
