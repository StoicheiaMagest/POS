package org.example;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

// The getters on PurchaseOccasion that a Receipt reads its data from.
public class PurchaseOccasionReceiptDataTest {

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

    // getCouponDiscountTotal

    @Test
    void couponDiscountTotalIsZeroBeforeDiscountsAreApplied() {
        Basket basket = new Basket();
        basket.addItem(itemWithPrice(150L));
        Customer member = new Customer("Astrid", "19900101-1234", true, false);
        member.addCoupon(new Coupon("20krOff", 20L, LocalDateTime.now().plusDays(1)));

        PurchaseOccasion purchase = new PurchaseOccasion(basket, member, createStore());

        assertEquals(0L, purchase.getCouponDiscountTotal());
    }

    @Test
    void couponDiscountTotalIsTheValueOfAUsedCoupon() {
        Basket basket = new Basket();
        basket.addItem(itemWithPrice(150L));
        Customer member = new Customer("Astrid", "19900101-1234", true, false);
        member.addCoupon(new Coupon("20krOff", 20L, LocalDateTime.now().plusDays(1)));

        PurchaseOccasion purchase = new PurchaseOccasion(basket, member, createStore());
        purchase.applyDiscounts();

        assertEquals(20L, purchase.getCouponDiscountTotal());
    }

    @Test
    void couponDiscountTotalIsTheSumOfSeveralUsedCoupons() {
        Basket basket = new Basket();
        basket.addItem(itemWithPrice(150L));
        Customer member = new Customer("Astrid", "19900101-1234", true, false);
        member.addCoupon(new Coupon("20krOff", 20L, LocalDateTime.now().plusDays(1)));
        member.addCoupon(new Coupon("30krOff", 30L, LocalDateTime.now().plusDays(1)));

        PurchaseOccasion purchase = new PurchaseOccasion(basket, member, createStore());
        purchase.applyDiscounts();

        assertEquals(50L, purchase.getCouponDiscountTotal());
        assertEquals(100L, purchase.getTotalPriceForPurchase());
    }

    @Test
    void couponDiscountTotalIgnoresExpiredCoupons() {
        Basket basket = new Basket();
        basket.addItem(itemWithPrice(150L));
        Customer member = new Customer("Astrid", "19900101-1234", true, false);
        Coupon expiredCoupon = mock(Coupon.class);
        when(expiredCoupon.isExpired()).thenReturn(true);
        member.addCoupon(expiredCoupon);

        PurchaseOccasion purchase = new PurchaseOccasion(basket, member, createStore());
        purchase.applyDiscounts();

        assertEquals(0L, purchase.getCouponDiscountTotal());
    }

    @Test
    void couponDiscountTotalIsZeroForNonMemberWithCoupon() {
        Basket basket = new Basket();
        basket.addItem(itemWithPrice(150L));
        Customer nonMember = new Customer("Stig", "19900101-1234", false, false);
        nonMember.addCoupon(new Coupon("20krOff", 20L, LocalDateTime.now().plusDays(1)));

        PurchaseOccasion purchase = new PurchaseOccasion(basket, nonMember, createStore());
        purchase.applyDiscounts();

        assertEquals(0L, purchase.getCouponDiscountTotal());
        assertEquals(150L, purchase.getTotalPriceForPurchase());
    }

    // a coupon bigger than what is left only counts for what it really took off,
    // otherwise the receipt lines would not add up to the total
    @Test
    void couponDiscountTotalIsCappedAtTheRemainingTotal() {
        Basket basket = new Basket();
        basket.addItem(itemWithPrice(150L));
        Customer member = new Customer("Astrid", "19900101-1234", true, false);
        member.addCoupon(new Coupon("200krOff", 200L, LocalDateTime.now().plusDays(1)));

        PurchaseOccasion purchase = new PurchaseOccasion(basket, member, createStore());
        purchase.applyDiscounts();

        assertEquals(150L, purchase.getCouponDiscountTotal());
        assertEquals(0L, purchase.getTotalPriceForPurchase());
    }

    // getSeniorDiscountAmount

    @Test
    void seniorDiscountAmountIsZeroBeforeDiscountsAreApplied() {
        Basket basket = new Basket();
        basket.addItem(itemWithPrice(150L));
        Customer seniorMember = new Customer("Greta", "19400101-1234", true, true);

        PurchaseOccasion purchase = new PurchaseOccasion(basket, seniorMember, createStore());

        assertEquals(0L, purchase.getSeniorDiscountAmount());
    }

    @Test
    void seniorDiscountAmountIsTenPercentForSeniorMember() {
        Basket basket = new Basket();
        basket.addItem(itemWithPrice(150L));
        Customer seniorMember = new Customer("Greta", "19400101-1234", true, true);

        PurchaseOccasion purchase = new PurchaseOccasion(basket, seniorMember, createStore());
        purchase.applyDiscounts();

        assertEquals(15L, purchase.getSeniorDiscountAmount());
        assertEquals(135L, purchase.getTotalPriceForPurchase());
    }

    @Test
    void seniorDiscountAmountIsZeroForSeniorWhoIsNotAMember() {
        Basket basket = new Basket();
        basket.addItem(itemWithPrice(150L));
        Customer seniorNonMember = new Customer("Greta", "19400101-1234", false, true);

        PurchaseOccasion purchase = new PurchaseOccasion(basket, seniorNonMember, createStore());
        purchase.applyDiscounts();

        assertEquals(0L, purchase.getSeniorDiscountAmount());
        assertEquals(150L, purchase.getTotalPriceForPurchase());
    }

    @Test
    void seniorDiscountAmountIsZeroForMemberWhoIsNotASenior() {
        Basket basket = new Basket();
        basket.addItem(itemWithPrice(150L));
        Customer member = new Customer("Astrid", "19900101-1234", true, false);

        PurchaseOccasion purchase = new PurchaseOccasion(basket, member, createStore());
        purchase.applyDiscounts();

        assertEquals(0L, purchase.getSeniorDiscountAmount());
        assertEquals(150L, purchase.getTotalPriceForPurchase());
    }

    // the senior discount comes last, on what is left after the coupon
    @Test
    void seniorDiscountAmountIsCalculatedAfterCoupons() {
        Basket basket = new Basket();
        basket.addItem(itemWithPrice(150L));
        Customer seniorMember = new Customer("Greta", "19400101-1234", true, true);
        seniorMember.addCoupon(new Coupon("20krOff", 20L, LocalDateTime.now().plusDays(1)));

        PurchaseOccasion purchase = new PurchaseOccasion(basket, seniorMember, createStore());
        purchase.applyDiscounts();

        // 150 - 20 = 130, and 10% of 130 = 13
        assertEquals(13L, purchase.getSeniorDiscountAmount());
        assertEquals(117L, purchase.getTotalPriceForPurchase());
    }

    @Test
    void seniorDiscountAmountIsRoundedHalfUp() {
        Basket basket = new Basket();
        basket.addItem(itemWithPrice(155L));
        Customer seniorMember = new Customer("Greta", "19400101-1234", true, true);

        PurchaseOccasion purchase = new PurchaseOccasion(basket, seniorMember, createStore());
        purchase.applyDiscounts();

        // 10% of 155 = 15.5, rounded up to 16
        assertEquals(16L, purchase.getSeniorDiscountAmount());
        assertEquals(139L, purchase.getTotalPriceForPurchase());
    }
}
