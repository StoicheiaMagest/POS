package org.example;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PurchaseOccasionDiscountIntegrationTest {

    @Test
    void applying_group_discount_should_change_total_cost_of_purchase() {
         Basket basket = new Basket();
        basket.addItem(itemWithPrice(10000));
        basket.addItem(itemWithPrice(10000));
        basket.addItem(itemWithPrice(10000));

        //Discount discount = mock(Discount.class);
        //Map<String, Long> discMap = new Map<>();
       // when(discount.getAllDiscounts(basket.getItems())).thenReturn(discMap);

        PurchaseOccasion purchase = createPurchase(basket);
        //purchase.applyDiscount(discMap); detta kanske är med av en integrationtest

        assertEquals(9000, purchase.getTotalPriceForPurchace(), 0.001, "Total price should adhere to discount");
    }
    
    private Item itemWithPrice(long priceInOre) {
        Item item = mock(Item.class);
        when(item.getInclusivePrice()).thenReturn(priceInOre);
        return item;
    }

    private PurchaseOccasion createPurchase(Basket basket) {
        Customer customer =
                new Customer("Test Customer", "19900101-1234", false, false);
        Account account = mock(Account.class);

        return new PurchaseOccasion(basket, customer, account);
    }
}
