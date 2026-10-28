package org.example;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

public class CheckOutTest {

    private Store createStoreWithTwoArticles() {
        ArticleRegistry registry = new ArticleRegistry();

        Item milk = mock(Item.class);
        when(milk.getPrice()).thenReturn(100L);
        when(milk.getInclusivePrice()).thenReturn(100L);
        registry.registerItem(1001, milk);

        Item bread = mock(Item.class);
        when(bread.getPrice()).thenReturn(150L);
        when(bread.getInclusivePrice()).thenReturn(150L);
        registry.registerItem(1002, bread);

        BarcodeScanner scanner = new BarcodeScanner(registry);
        return new Store(registry, new Account(), new DiscountHandler(), scanner);
    }

    @Test
    public void scanGoodsAddsScannedItemsToBasket(){
        Store store = createStoreWithTwoArticles();
        CheckOut checkOut = new CheckOut(store);

        checkOut.scanGoods(1001, 1002);

        assertEquals(2, checkOut.getBasket().getItems().size());
        assertEquals(250L, checkOut.getBasket().getTotalPrice());
    }

    @Test
    public void payCreatesPurchaseOccasionFromThisCheckoutsBasketAndStore(){
        Store store = createStoreWithTwoArticles();
        CheckOut checkOut = new CheckOut(store);
        checkOut.scanGoods(1001, 1002);
        Customer customer = new Customer("Alex", "19900101-1234", false, false);

        PurchaseOccasion purchase = checkOut.pay(customer);

        assertEquals(250L, purchase.getTotalPriceForPurchase());
    }
}
