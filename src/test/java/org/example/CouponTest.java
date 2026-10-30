package org.example;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class CouponTest {

    @Test
    public void couponWithExpiryDateInThePastIsExpired(){
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
        Coupon coupon = new Coupon("20krOff", 20, yesterday);
        assertTrue(coupon.isExpired());
    }

    @Test
    public void couponWithExpiryDateInTheFutureIsNotExpired(){
        LocalDateTime tomorrow = LocalDateTime.now().plusDays(1);
        Coupon coupon = new Coupon("20krOff", 20, tomorrow);
        assertFalse(coupon.isExpired());
    }

    @Test
    public void createCouponWithoutNameIsInvalid(){
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Coupon("", 10, LocalDateTime.now().plusDays(1))
        );

        assertEquals("Invalid coupon name",
                exception.getMessage());
    }

    @Test
    public void createCouponWithNullNameIsInvalid(){
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Coupon(null, 10, LocalDateTime.now().plusDays(1))
        );

        assertEquals("Invalid coupon name",
                exception.getMessage());
    }

    @Test
    public void createCouponWithBlankNameIsInvalid(){
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Coupon("   ", 10, LocalDateTime.now().plusDays(1))
        );

        assertEquals("Invalid coupon name",
                exception.getMessage());
    }

    @Test
    public void createCouponWithNegativeReducitonIsInvalid(){
        //Coupon coupon = new Coupon("20krOff", -9, LocalDateTime.now().plusDays(1));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Coupon("20krOff", -9, LocalDateTime.now().plusDays(1))
        );

        assertEquals("Invalid reduction values",
                exception.getMessage());
    }

    //test for use coupon

    @Test 
    public void createValidCoupon(){
        Coupon coupon = new Coupon("20krOff", 20, LocalDateTime.now().plusDays(1));
        assertEquals("20krOff", coupon.getName());
        assertEquals(20, coupon.getReduction());
    }
}
