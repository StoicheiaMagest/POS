package org.example;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;

class PurchaseOccasionTest {

//    @Test
//    void receiptShowsZeroTotalForEmptyBasket() {
//        Basket basket = new Basket();
//
//        PurchaseOccasion purchase = createPurchase(basket);
//
//        assertEquals(
//                "Kvitto\nAntal varor: 0\nTotalt: 0,00 SEK",
//                purchase.printReceipt()
//        );
//    }

//    @Test
//    void receiptShowsTotalForOneItem() {
//        Basket basket = new Basket();
//        basket.addItem(itemWithPrice(9999));
//
//        PurchaseOccasion purchase = createPurchase(basket);
//
//        assertEquals(
//                "Kvitto\nAntal varor: 1\nTotalt: 99,99 SEK",
//                purchase.printReceipt()
//        );
//    }

//    @Test
//    void receiptShowsSumForMultipleItems() {
//        Basket basket = new Basket();
//        basket.addItem(itemWithPrice(1250));
//        basket.addItem(itemWithPrice(250));
//
//        PurchaseOccasion purchase = createPurchase(basket);
//
//        assertEquals(
//                "Kvitto\nAntal varor: 2\nTotalt: 15,00 SEK",
//                purchase.printReceipt()
//        );
//    }

//    @Test
//    void a_senior_discount_should_change_total_cost_of_purchase() {
//        Basket basket = new Basket();
//        basket.addItem(itemWithPrice(10000));
//
//        Discount discount = mock(Discount.class);
//        when(discount.getSeniorDiscountProcent()).thenReturn(10);
//
//        PurchaseOccasion purchase = createPurchaseOfSenior(basket);
//        if(purchase.getCustomer().getSeniorStatus()) {
//            purchase.applyDiscount(discount.getSeniorDiscountProcent());
//        }
//
//        assertEquals(9000, purchase.getTotalPriceForPurchace(), 0.001, "Total price should adhere to discount");
//    }

//    @Test
//    void applying_group_discount_should_change_total_cost_of_purchase() {
//         Basket basket = new Basket();
//        basket.addItem(itemWithPrice(10000));
//        basket.addItem(itemWithPrice(10000));
//        basket.addItem(itemWithPrice(10000));
//
//        Discount discount = mock(Discount.class);
//        HashMap<String, Long> discMap = new HashMap<>();
//        when(discount.getAllDiscounts(basket.getItems())).thenReturn(discMap);
//
//        PurchaseOccasion purchase = createPurchase(basket);
//        //purchase.applyDiscount(discMap); detta kanske är med av en integrationtest
//
//        assertEquals(9000, purchase.getTotalPriceForPurchace(), 0.001, "Total price should adhere to discount");
//    }

    private Item itemWithPrice(long priceInOre) {
        Item item = mock(Item.class);
        when(item.getInclusivePrice()).thenReturn(priceInOre);
        return item;
    }

    private PurchaseOccasion createPurchase(Basket basket) {
        Customer customer =
                new Customer("Test Customer", "19900101-1234", false, false);
        Account account = mock(Account.class);
        Discount discount = mock(Discount.class);

        return new PurchaseOccasion(basket, customer, account, discount);
    }

    private PurchaseOccasion createPurchaseOfSenior(Basket basket) {
        Customer customer =
                new Customer("Test Customer", "19900101-1234", false, true);
        Account account = mock(Account.class);
        Discount discount = mock(Discount.class);

        return new PurchaseOccasion(basket, customer, account, discount);
    }
}
