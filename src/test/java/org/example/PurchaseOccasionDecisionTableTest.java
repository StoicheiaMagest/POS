package org.example;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

/*
 * One test per row of the decision table (r1-r10).
 * Discounts are applied in priority order:
 * 1. flertartikelsrabatt, 2. membership check, 3. coupons, 4. senior discount.
 */
public class PurchaseOccasionDecisionTableTest {

    private Item article(String name, long priceInOre, String discountType) {
        Item item = mock(Item.class);
        when(item.getName()).thenReturn(name);
        when(item.getPrice()).thenReturn(priceInOre);
        when(item.getInclusivePrice()).thenReturn(priceInOre);
        when(item.getDiscountType()).thenReturn(discountType);
        return item;
    }

    private Store createStore() {
        ArticleRegistry registry = new ArticleRegistry();
        BarcodeScanner scanner = new BarcodeScanner(registry);
        return new Store(registry, new Account(), new DiscountHandler(), scanner);
    }

    // r2: multi-item discount, no membership -> 3 for 2 applies to everyone
    @Test
    void r2MultiItemDiscountAppliesForNonMember() {
        Store store = createStore();
        Item milk = article("Mjölk", 100L, "3FOR2");

        Basket basket = new Basket();
        basket.addItem(milk);
        basket.addItem(milk);
        basket.addItem(milk);

        Customer nonMember = new Customer("Stig", "19900101-1234", false, false);
        PurchaseOccasion purchase = new PurchaseOccasion(basket, nonMember, store);

        purchase.applyDiscounts();

        // 3 x 100 ore with one free = 200
        assertEquals(200L, purchase.getTotalPriceForPurchase());
    }

    // r1: nothing applies -> total stays the same
    @Test
    void r1NoDiscountConditionsKeepsTotalUnchanged() {
        Store store = createStore();
        Item bread = article("Bröd", 150L, null);

        Basket basket = new Basket();
        basket.addItem(bread);

        Customer nonMember = new Customer("Stig", "19900101-1234", false, false);
        PurchaseOccasion purchase = new PurchaseOccasion(basket, nonMember, store);

        purchase.applyDiscounts();

        assertEquals(150L, purchase.getTotalPriceForPurchase());
    }

    // r3: membership alone gives no discount
    @Test
    void r3MembershipAloneGivesNoDiscount() {
        Store store = createStore();
        Item bread = article("Bröd", 150L, null);

        Basket basket = new Basket();
        basket.addItem(bread);

        Customer member = new Customer("Astrid", "19900101-1234", true, false);
        PurchaseOccasion purchase = new PurchaseOccasion(basket, member, store);

        purchase.applyDiscounts();

        assertEquals(150L, purchase.getTotalPriceForPurchase());
    }
}
