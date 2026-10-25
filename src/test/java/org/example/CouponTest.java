package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CouponTest {

    @Test
    void percentCouponTakesPercentOfPrice() {
        Coupon coupon = Coupon.percentCoupon("10 %", 10, 0);
        assertEquals(50L, coupon.getDiscount(500));
    }

    @Test
    void percentCouponRoundsHalfUp() {
        Coupon coupon = Coupon.percentCoupon("10 %", 10, 0);
        assertEquals(100L, coupon.getDiscount(995)); // 99.5 -> 100
    }

    @Test
    void fixedCouponSubtractsAmount() {
        Coupon coupon = Coupon.fixedCoupon("20 kr", 200, 0);
        assertEquals(200L, coupon.getDiscount(500));
    }

    @Test
    void fixedCouponNeedsThreshold() {
        Coupon coupon = Coupon.fixedCoupon("20 kr", 200, 2000); // if (p >= 200) p -= 20
        assertEquals(0L, coupon.getDiscount(1999));
        assertEquals(200L, coupon.getDiscount(2000)); // boundary counts
    }

    @Test
    void discountNeverExceedsPrice() {
        Coupon big = Coupon.fixedCoupon("100 kr", 10000, 0);
        assertEquals(500L, big.getDiscount(500));

        Coupon hundredPercent = Coupon.percentCoupon("100 %", 100, 0);
        assertEquals(500L, hundredPercent.getDiscount(500));
    }

    @Test
    void couponIsConsumable() {
        Coupon coupon = Coupon.fixedCoupon("20 kr", 200, 0);
        assertFalse(coupon.isRedeemed());
        assertEquals(200L, coupon.getDiscount(500));

        coupon.redeem();

        assertTrue(coupon.isRedeemed());
        assertEquals(0L, coupon.getDiscount(500)); // used up, gives nothing more
    }

    @Test
    void invalidValuesThrow() {
        assertThrows(IllegalArgumentException.class, () -> Coupon.percentCoupon(null, 10, 0));
        assertThrows(IllegalArgumentException.class, () -> Coupon.fixedCoupon(" ", 200, 0));
        assertThrows(IllegalArgumentException.class, () -> Coupon.fixedCoupon("x", 0, 0));
        assertThrows(IllegalArgumentException.class, () -> Coupon.percentCoupon("x", 101, 0));
        assertThrows(IllegalArgumentException.class, () -> Coupon.fixedCoupon("x", 200, -1));
    }

    @Test
    void gettersReturnConstructorValues() {
        Coupon coupon = Coupon.fixedCoupon("20 kr", 200, 2000);
        assertEquals("20 kr", coupon.getName());
        assertEquals(Coupon.Type.FIXED, coupon.getType());
        assertEquals(200L, coupon.getValue());
        assertEquals(2000L, coupon.getMinPurchase());
    }
}
