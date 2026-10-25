package org.example;

import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

// One test per row (r1-r16) of the discount decision table.
// Basket without milk: 7 soaps x 500 ore = 3500.
// Basket with milk: 3 milks (3 for 2 -> 500 off) + 7 soaps = 5000, 4500 after item discount.
// Coupon: if (p >= 2000) p -= 200. Senior: always 10 % of what remains.
public class PurchaseOccasionDiscountIntegrationTest {

    private final Discount discount = new Discount();

    private Basket basketWithMilk() {
        Basket basket = new Basket();
        for (int i = 0; i < 3; i++) {
            basket.addItem(new UnitPricedArticle("Milk", 400, Vat.STANDARD, 111, DiscountType.THREE_FOR_TWO)); // 500 incl
        }
        return addSoaps(basket);
    }

    private Basket basketNoMilk() {
        return addSoaps(new Basket());
    }

    private Basket addSoaps(Basket basket) {
        for (int i = 0; i < 7; i++) {
            basket.addItem(new UnitPricedArticle("Soap", 400, Vat.STANDARD, 333)); // 500 incl
        }
        return basket;
    }

    private Customer customer(boolean member, boolean senior, boolean withCoupon) {
        Customer customer = new Customer("Test", "19900101-1234", member, senior);
        if (withCoupon) {
            customer.addCoupon(Coupon.fixedCoupon("20 kr", 200, 2000));
        }
        return customer;
    }

    private Map<String, Long> expected(Object... keysAndOre) {
        Map<String, Long> map = new LinkedHashMap<>();
        for (int i = 0; i < keysAndOre.length; i += 2) {
            map.put((String) keysAndOre[i], ((Number) keysAndOre[i + 1]).longValue());
        }
        return map;
    }

    // r1: no member, no flerrabatt -> nothing
    @Test
    void r1_nonMember_noItemDiscount_getsNothing() {
        assertEquals(expected(), discount.getAllDiscounts(basketNoMilk(), customer(false, false, false)));
    }

    // r2: no member, flerrabatt -> 3 for 2 (store wide, applies anyway)
    @Test
    void r2_nonMember_getsItemDiscountOnly() {
        assertEquals(expected("3 for 2 Milk", 500),
                discount.getAllDiscounts(basketWithMilk(), customer(false, false, false)));
    }

    // r3: member, no flerrabatt, no coupon -> nothing extra
    @Test
    void r3_member_nothingToApply_getsNothing() {
        assertEquals(expected(), discount.getAllDiscounts(basketNoMilk(), customer(true, false, false)));
    }

    // r4: member, flerrabatt, no coupon -> 3 for 2
    @Test
    void r4_member_getsItemDiscount() {
        assertEquals(expected("3 for 2 Milk", 500),
                discount.getAllDiscounts(basketWithMilk(), customer(true, false, false)));
    }

    // r5: member, no flerrabatt, coupon -> if (p >= 200) p -= 20
    @Test
    void r5_memberWithCoupon_getsCouponDiscount() {
        assertEquals(expected("Coupon 20 kr", 200),
                discount.getAllDiscounts(basketNoMilk(), customer(true, false, true)));
    }

    // r6: member, flerrabatt, coupon -> 3 for 2, then coupon on the remainder
    @Test
    void r6_memberWithCouponAndItemDiscount_getsBoth() {
        assertEquals(expected("3 for 2 Milk", 500, "Coupon 20 kr", 200),
                discount.getAllDiscounts(basketWithMilk(), customer(true, false, true)));
    }

    // r7: member, senior -> 10 %
    @Test
    void r7_memberSenior_getsSeniorDiscount() {
        assertEquals(expected("Senior 10%", 350),
                discount.getAllDiscounts(basketNoMilk(), customer(true, true, false)));
    }

    // r8: member, senior, flerrabatt -> 3 for 2, then 10 % on the remainder
    @Test
    void r8_memberSeniorWithItemDiscount_getsBoth() {
        assertEquals(expected("3 for 2 Milk", 500, "Senior 10%", 450),
                discount.getAllDiscounts(basketWithMilk(), customer(true, true, false)));
    }

    // r9: member, senior, coupon -> coupon, then 10 % on the remainder
    @Test
    void r9_memberSeniorWithCoupon_getsBoth() {
        assertEquals(expected("Coupon 20 kr", 200, "Senior 10%", 330),
                discount.getAllDiscounts(basketNoMilk(), customer(true, true, true)));
    }

    // r10: everything -> 3 for 2, coupon, then senior 10 % last
    @Test
    void r10_memberSeniorWithEverything_stacksInPriorityOrder() {
        Map<String, Long> discounts =
                discount.getAllDiscounts(basketWithMilk(), customer(true, true, true));

        assertEquals(expected("3 for 2 Milk", 500, "Coupon 20 kr", 200, "Senior 10%", 430),
                discounts);
        assertEquals(
                List.of("3 for 2 Milk", "Coupon 20 kr", "Senior 10%"),
                new java.util.ArrayList<>(discounts.keySet()));
    }

    // 2 kr off is a member discount. Two breads: 2 x 1000 incl = 2000.
    private Basket basketWithBread() {
        Basket basket = new Basket();
        for (int i = 0; i < 2; i++) {
            basket.addItem(new UnitPricedArticle("Bread", 800, Vat.STANDARD, 222, DiscountType.TWO_KR_OFF));
        }
        return basket;
    }

    // r11: no member, 2 kr off item -> nothing, the discount is for members only
    @Test
    void r11_nonMember_getsNoTwoKrOff() {
        assertEquals(expected(), discount.getAllDiscounts(basketWithBread(), customer(false, false, false)));
        assertEquals(expected(), discount.getAllDiscounts(basketWithBread(), null));
    }

    // r12: member, 2 kr off item -> 2 kr off per item
    @Test
    void r12_member_getsTwoKrOff() {
        assertEquals(expected("2 kr off on Bread", 400),
                discount.getAllDiscounts(basketWithBread(), customer(true, false, false)));
    }

    // r13: senior but no member, 2 kr off item -> nothing, neither 2 kr off nor the senior discount
    @Test
    void r13_seniorWithoutMembership_getsNoTwoKrOffAndNoSeniorDiscount() {
        assertEquals(expected(), discount.getAllDiscounts(basketWithBread(), customer(false, true, false)));
    }

    // r14: member and senior, 2 kr off item -> 2 kr off, then 10 % on the remainder
    @Test
    void r14_memberSenior_getsTwoKrOffThenSeniorDiscount() {
        assertEquals(expected("2 kr off on Bread", 400, "Senior 10%", 160),
                discount.getAllDiscounts(basketWithBread(), customer(true, true, false)));
    }

    // r15: member, 2 kr off item that costs less than 2 kr -> the discount stops at the price, total 0
    @Test
    void r15_member_twoKrOffIsNeverMoreThanTheItemPrice() {
        Basket basket = new Basket();
        for (int i = 0; i < 2; i++) {
            basket.addItem(new UnitPricedArticle("Pencil", 120, Vat.STANDARD, 223, DiscountType.TWO_KR_OFF)); // 150 incl
        }

        assertEquals(expected("2 kr off on Pencil", 300),
                discount.getAllDiscounts(basket, customer(true, false, false)));
        assertEquals(0, new PurchaseOccasion(basket, customer(true, false, false), new Account(0), discount)
                .getTotalPrice());
    }

    // The 2 kr off cap must not let the discount on a cheap item lower the price of other items
    @Test
    void r16_member_cheapTwoKrOffItemDoesNotReduceTheOtherItems() {
        Basket basket = new Basket();
        basket.addItem(new UnitPricedArticle("Pencil", 120, Vat.STANDARD, 223, DiscountType.TWO_KR_OFF)); // 150 incl
        basket.addItem(new UnitPricedArticle("Soap", 400, Vat.STANDARD, 333)); // 500 incl

        assertEquals(500, new PurchaseOccasion(basket, customer(true, false, false), new Account(0), discount)
                .getTotalPrice());
    }

    // The total of a non-member is not reduced by a member discount
    @Test
    void nonMemberPaysFullPriceForTwoKrOffItems() {
        PurchaseOccasion occasion = new PurchaseOccasion(
                basketWithBread(), customer(false, true, false), new Account(0), discount);

        assertEquals(2000, occasion.getTotalPrice());
    }

    // The purchase total is subtotal minus all discount lines.
    @Test
    void purchaseOccasionTotalSubtractsAllDiscounts() {
        PurchaseOccasion occasion = new PurchaseOccasion(
                basketWithMilk(), customer(true, true, true), new Account(0), discount);

        assertEquals(5000 - 500 - 200 - 430, occasion.getTotalPrice());
        assertEquals(occasion.getTotalPrice(), occasion.getTotalPriceForPurchace());
    }
}
