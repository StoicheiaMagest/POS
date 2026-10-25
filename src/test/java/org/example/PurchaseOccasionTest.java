package org.example;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

    // A senior member always gets 10 % on top of the other discounts.
    @Test
    void a_senior_discount_should_change_total_cost_of_purchase() {
        Basket basket = new Basket();
        basket.addItem(new UnitPricedArticle("Soap", 400, Vat.STANDARD, 333)); // 500 incl

        PurchaseOccasion purchase = new PurchaseOccasion(
                basket, new Customer("Stig", "19500101-1234", true, true),
                new Account(0), new Discount());

        assertEquals(450, purchase.getTotalPriceForPurchace());
    }

    @Test
    void successfulPaymentRedeemsTheCoupon() {
        Basket basket = new Basket();
        basket.addItem(new UnitPricedArticle("Soap", 400, Vat.STANDARD, 333)); // 500 incl

        Customer member = new Customer("Anna", "19900101-1234", true, false);
        Coupon coupon = Coupon.fixedCoupon("20 kr", 200, 0);
        member.addCoupon(coupon);

        Account customerAccount = new Account(10000);
        PurchaseOccasion purchase = new PurchaseOccasion(
                basket, member, mock(Account.class), new Discount());

        assertTrue(purchase.pay(new Card(customerAccount)));
        assertTrue(coupon.isRedeemed());
        assertNull(member.getFirstAvailableCoupon());
    }

    @Test
    void failedPaymentDoesNotRedeemTheCoupon() {
        Basket basket = new Basket();
        basket.addItem(new UnitPricedArticle("Soap", 400, Vat.STANDARD, 333)); // 500 incl

        Customer member = new Customer("Anna", "19900101-1234", true, false);
        Coupon coupon = Coupon.fixedCoupon("20 kr", 200, 0);
        member.addCoupon(coupon);

        Account emptyAccount = new Account(0); // cannot pay 300 ore
        PurchaseOccasion purchase = new PurchaseOccasion(
                basket, member, mock(Account.class), new Discount());

        assertFalse(purchase.pay(new Card(emptyAccount)));
        assertFalse(coupon.isRedeemed());
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
