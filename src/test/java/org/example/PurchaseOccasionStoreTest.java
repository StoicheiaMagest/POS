package org.example;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

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
}
