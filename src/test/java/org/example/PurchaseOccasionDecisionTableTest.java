package org.example;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

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

    // r4: multi-item discount plus membership -> only the 3 for 2, no extra member benefit
    @Test
    void r4MultiItemWithMemberGivesOnlyTheMultiItemDiscount() {
        Store store = createStore();
        Item milk = article("Mjölk", 100L, "3FOR2");

        Basket basket = new Basket();
        basket.addItem(milk);
        basket.addItem(milk);
        basket.addItem(milk);

        Customer member = new Customer("Astrid", "19900101-1234", true, false);
        PurchaseOccasion purchase = new PurchaseOccasion(basket, member, store);

        purchase.applyDiscounts();

        // 3 x 100 ore with one free = 200, membership adds nothing
        assertEquals(200L, purchase.getTotalPriceForPurchase());
    }

    // r5: member with a valid coupon -> the coupon reduction comes off
    @Test
    void r5MemberCouponReducesTotal() {
        Store store = createStore();
        Item bread = article("Bröd", 150L, null);

        Basket basket = new Basket();
        basket.addItem(bread);

        Customer member = new Customer("Astrid", "19900101-1234", true, false);
        member.addCoupon(new Coupon("20krOff", 20L, LocalDateTime.now().plusDays(1)));

        PurchaseOccasion purchase = new PurchaseOccasion(basket, member, store);

        purchase.applyDiscounts();

        assertEquals(130L, purchase.getTotalPriceForPurchase());
    }

    // r6: multi-item discount and coupon -> both apply, multi item first
    @Test
    void r6MultiItemAndCouponBothApply() {
        Store store = createStore();
        Item milk = article("Mjölk", 100L, "3FOR2");

        Basket basket = new Basket();
        basket.addItem(milk);
        basket.addItem(milk);
        basket.addItem(milk);

        Customer member = new Customer("Astrid", "19900101-1234", true, false);
        member.addCoupon(new Coupon("20krOff", 20L, LocalDateTime.now().plusDays(1)));

        PurchaseOccasion purchase = new PurchaseOccasion(basket, member, store);

        purchase.applyDiscounts();

        // 3 x 100 - 100 (3 for 2) - 20 (coupon) = 180
        assertEquals(180L, purchase.getTotalPriceForPurchase());
    }

    // non-members may keep coupons but never get a coupon discount (table: n/a)
    @Test
    void nonMemberCouponHasNoEffectAndStaysUnused() {
        Store store = createStore();
        Item bread = article("Bröd", 150L, null);

        Basket basket = new Basket();
        basket.addItem(bread);

        Customer nonMember = new Customer("Stig", "19900101-1234", false, false);
        Coupon coupon = new Coupon("20krOff", 20L, LocalDateTime.now().plusDays(1));
        nonMember.addCoupon(coupon);

        PurchaseOccasion purchase = new PurchaseOccasion(basket, nonMember, store);

        purchase.applyDiscounts();

        assertEquals(150L, purchase.getTotalPriceForPurchase());
        assertTrue(nonMember.getCoupons().contains(coupon));
    }

    // a member may use as many valid coupons as they want
    @Test
    void allValidCouponsOfMemberAreUsed() {
        Store store = createStore();
        Item bread = article("Bröd", 150L, null);

        Basket basket = new Basket();
        basket.addItem(bread);

        Customer member = new Customer("Astrid", "19900101-1234", true, false);
        member.addCoupon(new Coupon("20krOff", 20L, LocalDateTime.now().plusDays(1)));
        member.addCoupon(new Coupon("30krOff", 30L, LocalDateTime.now().plusDays(1)));

        PurchaseOccasion purchase = new PurchaseOccasion(basket, member, store);

        purchase.applyDiscounts();

        // 150 - 20 - 30 = 100
        assertEquals(100L, purchase.getTotalPriceForPurchase());
    }

    // the total never goes below zero: a coupon bigger than the balance
    // is still used up and the total lands on exactly zero
    @Test
    void couponBiggerThanBalanceBringsTotalToZero() {
        Store store = createStore();
        Item gum = article("Tuggummi", 16L, null);

        Basket basket = new Basket();
        basket.addItem(gum);

        Customer member = new Customer("Astrid", "19900101-1234", true, false);
        Coupon bigCoupon = new Coupon("20krOff", 20L, LocalDateTime.now().plusDays(1));
        member.addCoupon(bigCoupon);

        PurchaseOccasion purchase = new PurchaseOccasion(basket, member, store);

        purchase.applyDiscounts();

        assertEquals(0L, purchase.getTotalPriceForPurchase());
        assertFalse(member.getCoupons().contains(bigCoupon));
    }

    // r7: member and senior -> 10 percent off, nothing else applies
    @Test
    void r7SeniorMemberGetsTenPercentOff() {
        Store store = createStore();
        Item bread = article("Bröd", 200L, null);

        Basket basket = new Basket();
        basket.addItem(bread);

        Customer seniorMember = new Customer("Greta", "19500101-1234", true, true);

        PurchaseOccasion purchase = new PurchaseOccasion(basket, seniorMember, store);

        purchase.applyDiscounts();

        // 200 - 10% = 180
        assertEquals(180L, purchase.getTotalPriceForPurchase());
    }

    // r8: multi-item discount and senior member -> 3 for 2 first, then 10 percent
    @Test
    void r8MultiItemThenSeniorDiscount() {
        Store store = createStore();
        Item milk = article("Mjölk", 100L, "3FOR2");

        Basket basket = new Basket();
        basket.addItem(milk);
        basket.addItem(milk);
        basket.addItem(milk);

        Customer seniorMember = new Customer("Greta", "19500101-1234", true, true);

        PurchaseOccasion purchase = new PurchaseOccasion(basket, seniorMember, store);

        purchase.applyDiscounts();

        // 3 x 100 - 100 (3 for 2) = 200, then -10% = 180
        assertEquals(180L, purchase.getTotalPriceForPurchase());
    }

    // r9: coupon and senior member -> fixed reduction first, then 10 percent
    @Test
    void r9CouponThenSeniorDiscount() {
        Store store = createStore();
        Item bread = article("Bröd", 200L, null);

        Basket basket = new Basket();
        basket.addItem(bread);

        Customer seniorMember = new Customer("Greta", "19500101-1234", true, true);
        seniorMember.addCoupon(new Coupon("20krOff", 20L, LocalDateTime.now().plusDays(1)));

        PurchaseOccasion purchase = new PurchaseOccasion(basket, seniorMember, store);

        purchase.applyDiscounts();

        // 200 - 20 (coupon) = 180, then -10% = 162
        assertEquals(162L, purchase.getTotalPriceForPurchase());
    }

    // r10: all conditions -> 3 for 2, then coupon, then 10 percent senior discount
    @Test
    void r10AllDiscountsApplyInPriorityOrder() {
        Store store = createStore();
        Item milk = article("Mjölk", 100L, "3FOR2");

        Basket basket = new Basket();
        basket.addItem(milk);
        basket.addItem(milk);
        basket.addItem(milk);

        Customer seniorMember = new Customer("Greta", "19500101-1234", true, true);
        seniorMember.addCoupon(new Coupon("20krOff", 20L, LocalDateTime.now().plusDays(1)));

        PurchaseOccasion purchase = new PurchaseOccasion(basket, seniorMember, store);

        purchase.applyDiscounts();

        // 3 x 100 - 100 (3 for 2) - 20 (coupon) = 180, then -10% = 162
        assertEquals(162L, purchase.getTotalPriceForPurchase());
    }

    // senior discount requires membership too: a non-member senior gets nothing
    @Test
    void seniorWithoutMembershipGetsNoSeniorDiscount() {
        Store store = createStore();
        Item bread = article("Bröd", 200L, null);

        Basket basket = new Basket();
        basket.addItem(bread);

        Customer seniorNonMember = new Customer("Greta", "19500101-1234", false, true);

        PurchaseOccasion purchase = new PurchaseOccasion(basket, seniorNonMember, store);

        purchase.applyDiscounts();

        assertEquals(200L, purchase.getTotalPriceForPurchase());
    }
}
