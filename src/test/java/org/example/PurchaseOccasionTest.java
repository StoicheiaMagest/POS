package org.example;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PurchaseOccasionTest {

    private final Account account = mock(Account.class);
    private final Customer customer_member = mock(Customer.class);
    private final Customer customer_senior = mock(Customer.class);
    private final Customer customer_regular = mock(Customer.class);
    private Coupon coupon = mock(Coupon.class);

    private Item itemWithPrice(long priceInOre) {
        Item item = mock(Item.class);
        when(item.getPrice()).thenReturn(priceInOre);
        return item;
    }

    private PurchaseOccasion createPurchase(Basket basket, Customer customer) {
        return new PurchaseOccasion(basket, customer, account);
    }

    @Test
    void receiptShowsZeroTotalForEmptyBasket() {
        Basket basket = new Basket();

        PurchaseOccasion purchase = createPurchase(basket, customer_regular);

        assertEquals(
                "Kvitto\nAntal varor: 0\nTotalt: 0,00 SEK",
                purchase.printReceipt()
        );
    }

    @Test
    void receiptShowsTotalForOneItem() {
        Basket basket = new Basket();
        basket.addItem(itemWithPrice(9999));

        //misslyckas för item behöver hashcode?
        PurchaseOccasion purchase = createPurchase(basket, customer_regular);

        assertEquals(
                "Kvitto\nAntal varor: 1\nTotalt: 99,99 SEK",
                purchase.printReceipt()
        );
    }

    @Test
    void receiptShowsSumForMultipleItems() {
        Basket basket = new Basket();
        basket.addItem(itemWithPrice(1250));
        basket.addItem(itemWithPrice(250));

        PurchaseOccasion purchase = createPurchase(basket, customer_regular);

        assertEquals(
                "Kvitto\nAntal varor: 2\nTotalt: 15,00 SEK",
                purchase.printReceipt()
        );
    }

    @Test
    void getMembershipStatusShouldReturnTrueIfMember() {
        Basket basket = mock(Basket.class);

        when(customer_member.getMembershipStatus()).thenReturn(true);

        PurchaseOccasion purchase = createPurchase(basket, customer_member);
        assertTrue(purchase.getMembershipStatus());
    }

    @Test 
    void getSeniorStatusShouldReturnTrueIfSenior() {
        Basket basket = mock(Basket.class);

        when(customer_senior.getSeniorStatus()).thenReturn(true);

        PurchaseOccasion purchase = createPurchase(basket, customer_senior);
        assertTrue(purchase.getSeniorStatus());
    }

    @Test
    void getMemberAndSeniorShouldReturnFalseIfRegularCustomer() {
        Basket basket = mock(Basket.class);

        when(customer_regular.getMembershipStatus()).thenReturn(false);
        when(customer_regular.getSeniorStatus()).thenReturn(false);

        PurchaseOccasion purchase = createPurchase(basket, customer_regular);
        assertFalse(purchase.getMembershipStatus());
        assertFalse(purchase.getSeniorStatus());
    }

    @Test
    void aSeniorDiscountShouldChangeTotalCostOfPurchase() {
        Basket basket = new Basket();
        basket.addItem(itemWithPrice(10000));

        when(DiscountHandler.getSeniorDiscount()).thenReturn((long) 10);

        PurchaseOccasion purchase = createPurchase(basket, customer_senior);
        if(purchase.getSeniorStatus()) {
            purchase.applySeniorDiscount();
        }

        assertEquals(9000, purchase.getTotalPriceForPurchace(), 0.001, "Total price should adhere to discount");
    }

    @Test
    void usedCouponShouldChangeTotalCostOfPurchase() {
        /*ArrayList<Coupon> coupons;
        when(customer_regular.getCoupons()).thenReturn(coupons = new ArrayList<>(5));

        Basket basket = new Basket();
        basket.addItem(itemWithPrice(10000));

        PurchaseOccasion purchase = createPurchase(basket, customer_regular);

        purchase.useCoupon();

        assertEquals(9995, purchase.getTotalPriceForPurchace(), 0.001, "Total price should be 5 kr less");*/
    }
}
